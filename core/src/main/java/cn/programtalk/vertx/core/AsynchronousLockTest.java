package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;

public class AsynchronousLockTest extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        SharedData sharedData = vertx.sharedData();
        sharedData.getLock("lock1", res -> {
            if (res.succeeded()) {
                // 获得锁
                Lock lock = res.result();

                // 5秒后我们释放该锁以便其他人可以得到它
                System.out.println(Thread.currentThread().getName() + " 开始执行");
                vertx.setTimer(1000, tid -> lock.release());
                System.out.println(Thread.currentThread().getName() + " 执行完毕");
            } else {
                // 发生错误
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(AsynchronousLockTest.class, new DeploymentOptions().setInstances(3));
    }
}

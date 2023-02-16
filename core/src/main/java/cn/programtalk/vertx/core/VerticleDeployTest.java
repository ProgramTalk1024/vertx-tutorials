package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class VerticleDeployTest extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        System.out.println("start执行");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions(); // ①
        options.setInstances(3); // ②
        vertx.deployVerticle(new VerticleDeployTest(), options).onComplete(result -> {
            if (result.succeeded()) {
                System.out.println("发布完成, 发布ID=" + result.result()); // 发布完成, 发布ID=831a66f3-1e79-4de4-a886-f84647c53839
            } else {
                System.out.println("发布失败");
            }
        });

    }
}

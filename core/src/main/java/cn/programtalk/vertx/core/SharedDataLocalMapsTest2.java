package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;

public class SharedDataLocalMapsTest2 extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        LocalMap<Integer, Integer> localMap = vertx.sharedData().getLocalMap("map1");
        localMap.put(1, 1);
        vertx.deployVerticle(SharedDataLocalMapsTest2.class, new DeploymentOptions().setInstances(3));
    }

    @Override
    public void start() throws Exception {
        super.start();
        LocalMap<Integer, Integer> map1 = vertx.sharedData().getLocalMap("map1");
        System.out.println("map1中key=1的值是=" + map1.get(1));
    }
}

package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;

public class SharedDataLocalMapsTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        LocalMap<Integer, Integer> localMap = vertx.sharedData().getLocalMap("map1");
        localMap.put(1, 1);

        // 这个localMap就能够在一个程序中的其他处获取并使用
        LocalMap<Integer, Integer> localMap2 = vertx.sharedData().getLocalMap("map1");
        System.out.println(localMap2.get(1)); // 1

        // 如果再创建一个Vertx实例则无法获取到值
        Vertx vertx2 = Vertx.vertx();
        LocalMap<Integer, Integer> localMap3 = vertx2.sharedData().getLocalMap("map1");
        System.out.println(localMap3.get(1)); // null
    }
}

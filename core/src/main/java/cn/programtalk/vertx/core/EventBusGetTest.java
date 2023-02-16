package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusGetTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        // 通过 vertx.eventBus()获取事件总线
        EventBus eventBus = vertx.eventBus();
    }
}

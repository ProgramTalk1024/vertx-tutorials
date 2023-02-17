package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusPubSubTest1 {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("address1", event -> { // ①
            System.out.println("接收者收到信息1：" + event.body());
            event.reply("Fine thank you and you?");
        });
        eventBus.consumer("address1", event -> { //②
            System.out.println("接收者收到信息2：" + event.body());
            event.reply("Fine thank you and you?");
        });
        eventBus.publish("address1", "How are you?"); //③
    }
}

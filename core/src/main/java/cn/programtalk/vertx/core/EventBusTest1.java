package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusTest1 {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("address1", event -> {
            System.out.println("接收者收到信息：" + event.body());
            event.reply("Fine thank you and you?");
        });
        eventBus.request("address1", "How are you?", ar -> {
            if (ar.succeeded()) {
                System.out.println("发送者收到回复新：" + ar.result().body());
            }
        });
    }
}

package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

public class EventBusDeliveryOptionsTest1 {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        EventBus eventBus = vertx.eventBus();
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(100); //①
        eventBus.consumer("address1", event -> {
            // 注释掉，不应答
            //event.reply("应答"); // ②
        });
        eventBus.request("address1", "How are you?", options, event -> {
            if (event.succeeded()) {
                System.out.println("有应答");
            } else {
                System.out.println("失败"); // ③
            }
        });//③
    }
}

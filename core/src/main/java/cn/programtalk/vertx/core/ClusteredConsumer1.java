package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

public class ClusteredConsumer1 {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                EventBus eventBus = vertx.eventBus();
                eventBus.consumer("address1", event -> {
                    System.out.println("消费者收到消息：" + event.body());
                });
            } else {
                System.out.println("Failed: " + res.cause());
            }
        });
    }
}

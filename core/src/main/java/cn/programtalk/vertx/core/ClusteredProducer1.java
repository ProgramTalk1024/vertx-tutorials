package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

public class ClusteredProducer1 {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                EventBus eventBus = vertx.eventBus();
                String body = "body";
                System.out.println("生产者发送消息：" + body);
                eventBus.send("address1", body);
            } else {
                System.out.println("Failed: " + res.cause());
            }
        });
    }
}

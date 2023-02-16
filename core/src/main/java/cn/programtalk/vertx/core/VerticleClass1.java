package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

import java.util.concurrent.TimeUnit;

public class VerticleClass1 extends AbstractVerticle {
    HttpServer httpServer;
    @Override
    public void start() throws Exception {
        super.start();
        httpServer = getVertx().createHttpServer();
        httpServer.requestHandler(event -> {
            HttpServerResponse response = event.response();
            response.end("good");
        });
        httpServer.listen(8080);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        httpServer.close(event -> {
            if (event.succeeded()) {
                System.out.println("服务器停止成功");
            }
        });
    }

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        VerticleClass1 verticle = new VerticleClass1();
        vertx.deployVerticle(verticle);

        verticle.start();
        TimeUnit.SECONDS.sleep(10);
        verticle.stop();
    }
}

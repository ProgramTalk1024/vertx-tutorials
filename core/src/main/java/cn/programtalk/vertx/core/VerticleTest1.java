package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;

public class VerticleTest1 extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        vertx.createHttpServer()
                .requestHandler(req -> {
                    HttpServerResponse response = req.response();
                    response.putHeader("Content-type", "text/html; charset=utf-8");
                    response.end("如果浏览器展示了我，那么说明Http Server创建成功了!");
                })
                .listen(8080, result -> {
                    if (!result.succeeded()) {
                        System.out.println("启动失败");
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        VerticleTest1 verticle = new VerticleTest1();
        vertx.deployVerticle(verticle);
        verticle.start();
    }
}

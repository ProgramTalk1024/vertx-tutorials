package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

public class VertxInstanceTest_CreateHttpServer {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {
            HttpServerResponse response = request.response();
            response.putHeader("Content-Type", "application/json");
            response.end("{\"id\":1}");
        });
        httpServer.listen(8080);
    }
}

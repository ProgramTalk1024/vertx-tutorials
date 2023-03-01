package cn.programtalk.vertx.web;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class RouterTest {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        Route route = router.route();
        route.handler(ctx -> { // ①

            // 所有的请求都会调用这个处理器处理
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.setChunked(true);
            response.write("route1\n");
            // 此处不结束响应，而是继续执行以后的handler
            ctx.vertx().setTimer(5000, event -> {
                ctx.next();
            });
        });

        // 在定义一个handler
        route.handler(ctx -> { // ②

            // 所有的请求都会调用这个处理器处理
            HttpServerResponse response = ctx.response();
            // 写入响应并结束处理
            response.end("route2");
        });
        server.requestHandler(router).listen(8080);
    }
}

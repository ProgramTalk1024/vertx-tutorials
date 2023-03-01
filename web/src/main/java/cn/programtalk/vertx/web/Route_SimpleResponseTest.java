package cn.programtalk.vertx.web;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.List;

public class Route_SimpleResponseTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router
                .get("/v1/users")
                // 这个处理器将保证这个响应会被序列化成json
                // content type被设置成 "application/json"
                .respond(
                        ctx -> Future.succeededFuture(new JsonObject().put("hello", "world")));

        // 也可以使用POJO
        router
                .get("/pojo/")
                // 这个处理器将保证这个响应会被序列化成json
                // content type被设置成 "application/json"
                .respond(
                        ctx -> Future.succeededFuture(List.of(new User(1, "张飞"))));

        server.requestHandler(router).listen(8080);
    }
}

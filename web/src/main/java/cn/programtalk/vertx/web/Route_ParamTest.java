package cn.programtalk.vertx.web;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;

import java.util.List;

public class Route_ParamTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        // 查询参数
        router
                .post("/queryParam") // 路径参数通过
                .respond(
                        ctx -> {
                            // 通过queryParam方法执行key，获取参数值
                            List<String> queryParam = ctx.queryParam("id");
                            return Future.succeededFuture(queryParam);
                        });
        //
        router.post("/queryParam2").handler(ctx -> {
            ctx.request().setExpectMultipart(true);
            ctx.next();
        }).blockingHandler(ctx -> {
            // ... Do some blocking operation
            HttpServerRequest request = ctx.request();
            System.out.println(request.getParam("id"));
        });
        server.requestHandler(router).listen(8080);
    }
}

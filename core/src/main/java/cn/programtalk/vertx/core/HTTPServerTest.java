package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;

public class HTTPServerTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServerOptions options = new HttpServerOptions();
        // 网络活动日志
        options.setLogActivity(true);
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {
            String path = request.path();
            HttpMethod method = request.method();
            System.out.println("请求path=" + path + "，请求Method=" + method);
            HttpServerResponse response = request.response();
            if (path.equals("/users/1")) {
                response.end("user1");
            } else if (path.equals("/users/2")) {
                response.end("user2");
            } else {
                response.end("unknown");
            }
        });
        httpServer.listen(8080);
    }
}

package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

public class TCPServerTest {
    public static void main(String[] args) {
        // 创建Vertx实例
        Vertx vertx = Vertx.vertx();
        //配置 TCP 服务端
        NetServerOptions options = new NetServerOptions();
        options.setHost("localhost");
        NetServer netServer = vertx.createNetServer(options);
        // 接收传入连接的通知
        netServer.connectHandler(event -> System.out.println("有请求"));
        // 启动服务端监听，listen有多种方法，不一一说明。
        // 默认端口号是 0， 这也是一个特殊值，它告诉服务器随机选择并监听一个本地没有被占用的端口。
        netServer.listen(0, res -> { // 可以设置一个监听器区监听端口绑定情况（非必须，但建议这么做）
            if (res.succeeded()) {
                System.out.println("端口是：" + netServer.actualPort()); // 当端口设置0的时候此处可以处真正监听的端口
                System.out.println("启动成功"); // 启动成功
            } else {
                System.out.println("启动失败!");
            }
        });
    }
}

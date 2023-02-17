package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class TCPClientTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClientOptions options = new NetClientOptions().setConnectTimeout(10000);
        NetClient client = vertx.createNetClient(options);
        client.connect(56893, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                NetSocket socket = res.result();
                System.out.println(socket.localAddress());
            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());
            }
        });

    }
}

package cn.programtalk.vertx.core;

import io.netty.buffer.ByteBuf;
import io.vertx.core.buffer.Buffer;

import java.nio.ByteBuffer;

public class BuffersTest {
    public static void main(String[] args) {
        // 创建Buffer有多种方式
        Buffer b1 = Buffer.buffer();
        // 可以指定缓冲区大小，单位字节
        b1 = Buffer.buffer(1024);

        // 通过字符串创建,默认UTF-8编码
        Buffer b2 = Buffer.buffer("Vertx");
        System.out.println(b2); // Vertx

        // 通过字符串创建，同时可以指定指定编码
        Buffer b3 = Buffer.buffer("中国", "GBK");
        System.out.println(b3.toString("GBK")); // 中国

        //还可以通过字节数组创建Buffer
        Buffer b4 = Buffer.buffer(new byte[]{1, 3, 5});
        System.out.println(b4); // 

        //  从 Netty ByteBuf创建一个新的缓冲区，
        Buffer src = Buffer.buffer("Vertx");
        ByteBuf byteBuf = src.getByteBuf();
        Buffer clone = Buffer.buffer(byteBuf);
        System.out.println(clone); // Vertx

        // 追加buffer
        Buffer buffer = Buffer.buffer("1");
        buffer.appendString("2").appendString("3");
        System.out.println(buffer); // 123

        // setXXX 写buffer
        Buffer b5 = Buffer.buffer();
        b5.setBuffer(0, Buffer.buffer("1"));
        b5.setBuffer(2, Buffer.buffer("3"));
        System.out.println(b5); // 1 3  注意中间有NUL哦
        System.out.println(b5.toString().equals("1 3")); // false ,说明中间并不是空格

        // 读取Buffer
        Buffer buff = Buffer.buffer("1234");
        for (int i = 0; i < buff.length(); i ++) {
            System.out.println("string value at " + i + " is " + buff.getString(i, i+1));
            //string value at 0 is 1
            //string value at 1 is 2
            //string value at 2 is 3
            //string value at 3 is 4
        }
    }
}

package cn.programtalk.vertx.core;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

public class FutureCoordinationTest {
    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        FileSystem fs = vertx.fileSystem();

        Future<Void> future1 = fs
                .createFile("a1.txt"); // <1>
        Future<Void> future2 = fs
                .createFile("a2.txt"); // <2>

        CompositeFuture.all(future1, future2).onComplete(ar -> {// <3>
            if (ar.succeeded()) {
                // future1和future2都执行成功
                System.out.println("成功，开始将a1.txt和a2.txt打包为Zip格式");
            } else {
                // future1和future2至少一个失败
                System.out.println("失败");
            }
        });
    }
}

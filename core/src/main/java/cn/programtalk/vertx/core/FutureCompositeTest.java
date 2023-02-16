package cn.programtalk.vertx.core;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

import java.nio.charset.StandardCharsets;

public class FutureCompositeTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        FileSystem fs = vertx.fileSystem();
        Future<Void> future = fs
                .createFile("a.txt")      // ①
                .compose(v -> {      // ②
                    return fs.writeFile("a.txt", Buffer.buffer("文本内容".getBytes(StandardCharsets.UTF_8)));
                })
                .compose(v -> {      // ③
                    return fs.move("a.txt", "b.txt");
                });
    }
}

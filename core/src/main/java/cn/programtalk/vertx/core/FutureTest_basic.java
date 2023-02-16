package cn.programtalk.vertx.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;

public class FutureTest_basic {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        FileSystem fs = vertx.fileSystem();

        Future<FileProps> future = fs.props(".gitignore"); // ①

        future.onComplete((AsyncResult<FileProps> ar) -> { // ②
            if (ar.succeeded()) {
                FileProps props = ar.result();
                System.out.println("File size = " + props.size()); //③  File size = 310
            } else {
                System.out.println("Failure: " + ar.cause().getMessage());
            }
        });
    }
}

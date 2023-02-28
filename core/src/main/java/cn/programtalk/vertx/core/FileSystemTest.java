package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

public class FileSystemTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        // 获取文件系统
        FileSystem fileSystem = vertx.fileSystem();
        // 同步拷贝
        fileSystem.copyBlocking("input/from.txt", "output/to.txt");
        // 异步拷贝
        fileSystem.copy("input/from.txt", "output/to2.txt", event -> {
           if (event.succeeded()){
               System.out.println("拷贝完成"); // 拷贝完成
           }else {
               System.out.println("拷贝失败");
           }
        });
        // 读文件
        fileSystem.readFile("input/from.txt", event -> {
            if (event.succeeded()) {
                System.out.println(event.result().toString("UTF-8")); //content
            }
        });
        // 写文件异步
        fileSystem.writeFile("input/from.txt", Buffer.buffer("新写入的内容"), event -> {
            if (event.succeeded()) {
                System.out.println("写入成功");
            }
        });
        // 写文件异步
        fileSystem.writeFileBlocking("input/from.txt", Buffer.buffer("新写入的内容"));

        // 检查文件是否存在，异步
        fileSystem.exists("input/from.txt", event -> {
            if (event.succeeded()) {
                System.out.println("检查成功，文件是否存在？" + event.result());
            }else {
                System.out.println("检查失败");
            }
        });
        // 检查文件是否存在，同步
        boolean exists = fileSystem.existsBlocking("input/from.txt");
        System.out.println(exists);
    }
}

# Vert.x Core教程（一）
`Vert.X Core`是Vert.x工具的核心，它是一系列`Java APIs`的集合，它提供了很多的功能
* 编写TCP客户端和服务器。
* 编写HTTP客户端和服务器，支持WebSockets。
* 事件总线（Event bus）。
* 共享数据（Shared data）- 本地Maps和集群分布式Maps
* 定期的和延迟的动作（Action）。
* 部署、取消部署`Verticles`。
* Datagram Sockets
* DNS客户端
* 文件系统访问
* 高可用
* 本地传输
* 集群

上面出现了一些比较新的问题，或者存在不明白的地方？很正常，接下来逐步讲解。



# 准备

接下来的讲解要使用代码辅助，所以要建立项目，我这里建立的是一个Maven项目，如何创建项目本问不做讲解。

项目需要引入`vertx-core`依赖。

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-core</artifactId>
 <version>4.3.8</version>
</dependency>
```



# 核心接口类Vertx
Vert.x提供了一个接口`Vertx`，他是Vert.x的核心入口点，是Vert.x的控制中心，那么一个应用创建一个或者多个`Vertx`的实例是非常有必要的，那么如何创建呢？`VertX`提供了静态方法。

![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161406583.png)

示例代码如下：
```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;


public class VertxInstanceTest_Create {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
    }
}

```
创建这个实例有什么用呢？创建这个实例后，就可以使用该实例里的方法，比如实现一个Http服务器。
```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

public class VertxInstanceTest_CreateHttpServer {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {
            HttpServerResponse response = request.response();
            response.putHeader("Content-Type", "application/json");
            response.end("{\"id\":1}");
        });
        httpServer.listen(8080);
    }
}
```
打开浏览器访问：
![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161438504.png)

可以看到HTTP服务器正常访问，这里暂时不用关心Http服务如何实现，后面会单独讲解，知识要说明，Vertx实例可以实现Http服务器而已。

也可不不使用默认的`vertx()`方法，而是使用`vertx(VertxOptions options)`方法，该方法提供了自定义参数设置，用户可以自己决定设置什么参数。
下图中列出了该类的方法，不过目前并不会讲解每个方法的具体含义，之后的学习中都会慢慢基础到。
![VertxOptions](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161437522.png)

`clusteredVertx`提供了一个创建集群Vertx实例的方法，集群实例涉及到`Event Bus`和`Clusting`模块的内容，之后再讲解。

> 大多数应用中只需要一个Vertx实例即可，但这并不是说多Vertx实例无使用场景，比如事件总线隔离、或者客户端服务端之间隔离就可以使用多Vertx实例。


# Future
Vert.x4使用Future来响应异步结果。
## 基本使用
```java
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
```
①：此处定义了一个Future，用以接收文件读取事件结构。

②：onComplete方法用于定义文件读取完成的业务逻辑。

③：当文件读取成功的时候就会正常打印出文件的大小。

## 组合
compose可以将多个Future组成一个链条。

当当前的Future执行成功的时候，指定compose中传入的函数，该函数会返回一个Future,当这个返回的Future执行完成的时候，这个组合也就成功了。

如果当前的Future失败的时候，这个组合也就失败了。

接下来我通过一个例子来展示下Future组合的使用。

有一个业务：

1. 创建文件a.txt。
2. 向a.txt文件写入内容。
3. 将a.txt改名为b.txt。



示例代码如下：

```java
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
```

① 创建一个文件（a.txt）。

②  当 ① 执行完毕后（a.txt文件创建完成后）,执行 ②。

③  当 2 执行完毕后（a.txt文件创建完成后）,执行 ③。



## 协调

多个Future可以协调在一起，都成功能执行业务操作，Future的协调使用`CompositeFuture.all`将多个Future组装到一起，一同处理。

实例代码如下：

```
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
                .createFile("a1.txt"); // ①
        Future<Void> future2 = fs
                .createFile("a2.txt"); // ②

        CompositeFuture.all(future1, future2).onComplete(ar -> {// ③
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
```



① 创建第一个``Future``

② 创建第二个``Future``

③ 使用``CompositeFuture.all``协调两个``Future``，根据不同的情况编写不同的业务逻辑。




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




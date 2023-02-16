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

③  当 2 执行完毕后（a.txt文件写入内容）,执行 ③。



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

③ 使用``CompositeFuture.all``协调两个``Future``，都成功的时候才会执行③的代码。



# Verticles

Vert.x 内部提供了一个一个简单、可扩展、类似Actor模型（不懂的可以看看[这篇文章]([Actor模型 (qq.com)](https://mp.weixin.qq.com/s?__biz=MzI1Njk3MTAwNg==&mid=2247491229&idx=1&sn=ee00510c4e4e6fac36daedcad15bb015&chksm=ea1fc70cdd684e1a9c9ef12c88c47323b6526496271536a4272ee447e89f135143eb03e006c2&scene=27))）的部署、并发模型，开箱即用，使用它可以少写很多代码。



你可以将其理解为Java中的线程，一个 Vert.x 实例维护 N 个**事件循环线程(`event loop threads`) **（其中 N 默认为核心*2）。`Verticle`可以用 Vert.x 支持的任何语言编写, 单个应用程序可以包含用多种语言编写的`Verticle`。



既然说到Verticle是一个类似线程的东西，那么多线程所带来的通信问题，Verticle依然存在，在Vert.X中Verticle使用事件总线进行通信（Event Bus）。

这里有引出来几个新的概念

* 事件总线，这里不做过多解释，你可以将他理解为一个`Pub/Sub`的消息中间件即可。
* 事件循环线程(`event loop threads`) ：事件循环主要针对异步来说的，通常异步任务而言的。同步任务都在主线程上执行，形成一个函数调用栈（执行栈），而异步则先放到**任务队列**（**task queue**）里，后面会有专门的线程池从任务队列中获取任务进行执行，下图很好的展示了事件循环的工作情况。

![循环线程](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161114272.png)



## 创建Verticle

使用该模型必须实现``Verticle``接口或者继承``AbstractVerticle``抽象类，通常使用继承``AbstractVerticle``抽象类的方式，我们看下`Verticle`和`AbstractVerticle`的类图：

![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161550029.png)

`Verticle`中内置了一个`Vertx`的实例`Vertx vertx`和一个`Verticle`上下文实例`Context contenxt`。



代码示例如下：

```java
package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;

public class VerticleClass1 extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
```

通常重写`start`方法和`stop`方法即可，那么start方法有什么用呢？比如要启动一个http服务器。

```java
package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;

public class VerticleTest1 extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        vertx.createHttpServer()
                .requestHandler(req -> {
                    HttpServerResponse response = req.response();
                    response.putHeader("Content-type", "text/html; charset=utf-8");
                    response.end("如果浏览器展示了我，那么说明Http Server创建成功了!");
                })
                .listen(8080, result -> {
                    if (!result.succeeded()) {
                        System.out.println("启动失败");
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        VerticleTest1 verticle = new VerticleTest1();
        vertx.deployVerticle(verticle); //①
        verticle.start();
    }
}
```

①：这里使用到了`deployVerticle`方法，有什么用呢？其实它设置了Verticle的上下文（Contenxt），后面讲解。



启动程序后，访问浏览器，效果图如下：

![Verticle服务器](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161730002.png)



## 异步停止和启动

上面的例子中，重写了`start()`方法，这个方法是同步的，Vertx同样提供了异步的方法。

```java
public void start(Promise<Void> startPromise)
public void stop(Promise<Void> stopPromise)
```

这里我就不做演示了。



## Verticle类型

有种类型，标准类型和工作线程类型。



标准类型：是最常见和最有用的类型 - 它们始终使用事件循环线程执行。

工作线程池类型：他们通过工作线程池（Worker Pool）中的一个线程来运行，一个Verticle永远不会被多个线程并发执行，主要用于调用阻塞代码。如果不想使用工作线程池来运行阻塞代码，也可以使用使用事件轮询中的`vertx.executeBlocking`来运行。



## 发布Verticle

Vert.x提供了两种方式来发布Verticle，一种是编程的方式，一种是命令行的方式。

### 编程方式

#### 发布

上面其实已经使用过`deployVerticle`方法，他就是多种多态方法中的一个，通过该方法就能够发布Verticle。



类似如下代码：

```java
package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class VerticleDeployTest extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        System.out.println("start执行");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new VerticleDeployTest()).onComplete(result -> {
            if (result.succeeded()) {
                System.out.println("发布完成, 发布ID=" + result.result()); // 发布完成, 发布ID=831a66f3-1e79-4de4-a886-f84647c53839
            } else {
                System.out.println("发布失败");
            }
        });

    }
}
```

`deployVerticle`是一个Future，可以监听到结果，在onComplete中如果succeeded=true的时候，就会返回发布ID。



#### 取消发布

取消发布使用`undeploy`方法，需要传入发布ID，类似如下代码：

```java
vertx.undeploy(deploymentID, res -> {
  if (res.succeeded()) {
    System.out.println("Undeployed ok");
  } else {
    System.out.println("Undeploy failed!");
  }
});
```



#### 指定实例数

在发布的时候可以指定实例数，来告诉vertx，创建几个实例。

```java
package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class VerticleDeployTest extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        System.out.println("start执行");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions(); // ①
        options.setInstances(3); // ②
        vertx.deployVerticle(VerticleDeployTest.class, options).onComplete(result -> {// ③
            if (result.succeeded()) {
                System.out.println("发布完成, 发布ID=" + result.result()); // 发布完成, 发布ID=831a66f3-1e79-4de4-a886-f84647c53839
            } else {
                System.out.println("发布失败");
            }
        });

    }
}
```

①：定义了DeploymentOptions类型的发布参数。



②：通过`setInstances`方法设置实例数。



③：`deployVerticle`方法参数中设置`DeploymentOptions`。



运行结果如下：

![image-20230216180327856](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161803956.png)



其实实例数目限制的地方挺多，这里我就不一一展开。分章节去记录各种可能踩到的坑或者我已经踩到的坑!

比如将上面的代码③出修改下，将`VerticleDeployTest.class`修改为`new VerticleDeployTest()`则会报如下错误

![image-20230216180443198](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161804265.png)

这是因为通过`new`已经将实例创建出来了，所以不能指定超过1的实例数，②处只能设置为1。



### 命令行方式


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

上面出现了一些比较新的文字，或者存在不明白的地方？很正常，接下来逐步讲解。



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



## Content对象

当 Vert.x 传递一个事件给处理器或者调用 `Verticle` 的 start 或 stop 方法时， 它会关联一个 `Context` 对象来执行。通常来说这个context会是一个 **event-loop context**，它绑定到了一个特定的 Event Loop 线程上。所以在该context上执行的操作总是 在同一个 Event Loop 线程中。对于运行内联的阻塞代码的 Worker Verticle 来说，会关联一个 Worker Context，并且所有的操作运都会运行在 Worker 线程池的线程上。（译者注：每个 `Verticle` 在部署的时候都会被分配一个 `Context`（根据配置不同，可以是Event Loop Context 或者 Worker Context），之后此 `Verticle` 上所有的普通代码都会在此 `Context` 上执行（即对应的 Event Loop 或Worker 线程）。一个 `Context` 对应一个 Event Loop 线程（或 Worker 线程），但一个 Event Loop 可能对应多个 `Context`。）



# 事件总线

事件总线（Event Bus），是Vertx的神经系统，每一个 Vert.x 实例都有一个单独的 Event Bus 实例。

Event Bus构建了一个跨越多个服务器节点和多个浏览器的分布式点对点消息系统。

Event Bus支持发布/订阅、点对点、请求-响应的消息传递方式。

Event Bus的API很简单。基本上只涉及注册处理器、 注销处理器以及发送和发布(publish)消息。

## 获取事件总线

通过Vertx的eventBus方法获取

```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusGetTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        // 通过 vertx.eventBus()获取事件总线
        EventBus eventBus = vertx.eventBus();
    }
}
```

## 发布订阅模式 

发布订阅模式中，使用`publish`方法发布消息，使用`consumer`方法订阅消费消息，当然也可以使用`localConsumber`，他只适用于应用中，不能在集群中传递消息。

```
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusPubSubTest1 {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("address1", event -> { // ①
            System.out.println("接收者收到信息1：" + event.body());
            event.reply("Fine thank you and you?");
        });
        eventBus.consumer("address1", event -> { //②
            System.out.println("接收者收到信息2：" + event.body());
            event.reply("Fine thank you and you?");
        });
        eventBus.publish("address1", "How are you?"); //③
    }
}
```

运行结果：

![image-20230217093423234](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302170934385.png)



## 点对点消息

点对点消息就是说消息只能被一个接收者收到，这种模式下又分为有应答和没有应答的情况。应答使用`reply`方法，应答之后，发送这也会收到应答信息。

下面这个例子就是又应答的示例代码：

```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusTest1 {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("address1", event -> {
            System.out.println("接收者收到信息：" + event.body());
            event.reply("Fine thank you and you?");
        });
        eventBus.request("address1", "How are you?", ar -> {
            if (ar.succeeded()) {
                System.out.println("发送者收到回复新：" + ar.result().body());
            }
        });
    }
}
```

运行结果：

![image-20230216203100443](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302162031541.png)



无应答的情况，去掉`reply`相关代码即可。

## 发送配置

发送消息的时候，支持通过`DeliveryOptions`，比如对于有应答的情况，可以设置超时事件，如果超时，则会返回失败。

```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

public class EventBusDeliveryOptionsTest1 {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        EventBus eventBus = vertx.eventBus();
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(10); //①
        eventBus.consumer("address1", event -> {
            // 注释掉，不应答
            //event.reply("应答"); // ②
        });
        eventBus.request("address1", "How are you?", options, event -> {
            if (event.succeeded()) {
                System.out.println("有应答");
            } else {
                System.out.println("失败"); // ③
            }
        });//③
    }
}
```

①：定义10秒超时



②：一直没有应答



③：10秒后打印失败。



## 集群情况

上面的示例都是在一个Vertx示实例下，如果我有两个进程，一个发送消息，一个消费消息呢？Vertx也是支持的，接下来模拟下。



首先创建两个类，一个`ClusteredProducer1`，一个`ClusteredConsumer1`，在这两个类中Vertx实例要使用`clusteredVertx`来创建。



代码分别如下：



**ClusteredProducer1**：

```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

public class ClusteredProducer1 {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                EventBus eventBus = vertx.eventBus();
                String body = "body";
                System.out.println("生产者发送消息：" + body);
                eventBus.send("address1", body);
            } else {
                System.out.println("Failed: " + res.cause());
            }
        });
    }
}
```



**ClusteredConsumer1**:

```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

public class ClusteredConsumer1 {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                EventBus eventBus = vertx.eventBus();
                eventBus.consumer("address1", event -> {
                    System.out.println("消费者收到消息：" + event.body());
                });
            } else {
                System.out.println("Failed: " + res.cause());
            }
        });
    }
}
```



运行测试，先启动消费者，再启动生产者，会出现如下错误：

![image-20230217100119461](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171001541.png)



这是为什么呢？单个Vertx实例下为什么没有这个问题呢？其实仔细想想就能明白原因，既然是发送消息，那么起码发送消息的端信息得知道吧，比如上面的`address`参数。



在单一Vertx实例下，是存储在内存中的，那么集群就得有一个共享存储。在Vertx.x中就叫做`ClusterManager`，并且提供了几个集群管理器，比如`Hazelcast Clustering`,`Apache Zookeeper Clustering`等，比如我这里使用前者。



需要引入依赖：

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-hazelcast</artifactId>
</dependency>
```

再次运行则会正常工作：



生产者：

![生产者](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171013131.png)



消费者：

![消费者](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171013933.png)



这些集群管理器会在以后单独讲解。



# JsonObject

Vertx提供了一个`JSON`处理工具，就是`JsonObject`对象，接下来我以一个例子，来说明下，特别要注意代码中的说明文字。

```java
package cn.programtalk.vertx.core;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonObjectTest {
    public static void main(String[] args) {
        // 创建空JsonObject对象
        JsonObject jo1 = new JsonObject();
        System.out.println(jo1); // {}

        // 创建JsonObject对象，传入字符串
        JsonObject jo2 = new JsonObject("{\"id\":1}");
        System.out.println(jo2); // {"id":1}

        // 创建JsonObject对象，传入Map
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        JsonObject jo3 = new JsonObject(map);
        System.out.println(jo3); // {"id":1}

        // 创建JsonObject对象，传入Buffer
        Buffer buffer = Buffer.buffer("{\"id\":1}".getBytes(StandardCharsets.UTF_8));
        JsonObject jo4 = new JsonObject(map);
        System.out.println(jo4); // {"id":1}

        // 使用put设置值
        jo1.put("id", 1);
        jo1.put("name", "成吉思汗");

        // 通过key获取值
        System.out.println(jo1.getString("name")); // 成吉思汗
        System.out.println(jo1.getInteger("id")); // 1

        // 判断key是否存在
        System.out.println(jo1.containsKey("@$##@"));

        // JsonObject转换为POJO
        User user = jo1.mapTo(User.class); // User{id=1, name='成吉思汗'}
        System.out.println(user);

        // 也可以将对象转化为JsonObject
        System.out.println(JsonObject.mapFrom(user)); // {"id":1,"name":"成吉思汗"}

        // 合并使用mergeIn方法，比如将B合并进入A，则调用A.mergeIn(B)，合并也支持深度参数，也就是说如果json是嵌套的，可以设置是否合并子json对象，以及最深合并几层。
        JsonObject m1 = new JsonObject("{\"id\":2, \"sex\":\"男\"}");
        JsonObject mergeResult = jo1.mergeIn(m1);
        System.out.println(mergeResult); // {"id":2,"name":"成吉思汗","sex":"男"}
        System.out.println(jo1); // {"id":2,"name":"成吉思汗","sex":"男"}
        System.out.println(jo1 == mergeResult); // true,说明返回的就是jo1。

        // 深拷贝copy方法
        JsonObject copy = jo1.copy();
        System.out.println(copy); // {"id":2,"name":"成吉思汗","sex":"男"}

        // 清空
        jo1.clear();
        System.out.println(jo1); // {}

        // JsonArray，支持字符串、List和Buffer参数
        JsonArray ja1 = new JsonArray("[{\"id\":1}]");
        System.out.println(ja1);

        // 向数组添加元素, 使用add 或者addAll方法
        ja1.add("1");
        System.out.println(ja1); // [{"id":1},"1"]

        // 获取值,通过索引值
        System.out.println(ja1.getJsonObject(0)); // {"id":1}
        System.out.println(ja1.getString(1)); // 1

        //Json 指针
        JsonPointer pointer1 = JsonPointer.from("/hello/world");
        // Build a pointer manually
        JsonPointer pointer2 = JsonPointer.create()
                .append("hello")
                .append("world");

    }
}

class User {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
```



# Buffers

`Buffers`是Vert.x提供的一个数据类型，也是其内部用的最多的数据类型，`Buffer` 是一个可以被读取或写入的，包含0个或多个字节的序列，并且能够根据写入的字节自动扩容。 您也可以将 `Buffer` 想象成一个智能的字节数组。

## 创建Buffers

`Buffer`的创建有多种方式

![创建Buffer](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171348148.png)



可以看到支持`byte[]`、`String`， `ByteBuf`类型。

```java
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
```



## 追加Buffer

可以使用 `appendXXX` 方法追加数据到Buffer。

```java
// 追加buffer
Buffer buffer = Buffer.buffer("1");
buffer.appendString("2").appendString("3");
System.out.println(buffer); // 123
```

## 写Buffer

也可以使用`setXXX`方法通过位置写Buffer。

```java
// setXXX 写buffer
Buffer b5 = Buffer.buffer();
b5.setBuffer(0, Buffer.buffer("1"));
b5.setBuffer(2, Buffer.buffer("3"));
System.out.println(b5); // 1 3  注意中间有NUL哦
System.out.println(b5.toString().equals("1 3")); // false ,说明中间并不是空格
```

## 读取Buffer

```java
 // 读取Buffer
 Buffer buff = Buffer.buffer("1234");
 for (int i = 0; i < buff.length(); i ++) {
 System.out.println("string value at " + i + " is " + buff.getString(i, i+1));
 //string value at 0 is 1
 //string value at 1 is 2
 //string value at 2 is 3
 //string value at 3 is 4
 }
```

## Buffer长度

可使用 `length` 方法获取Buffer长度， Buffer的长度值是Buffer中包含的字节的最大索引 + 1。

```java
// Buffer长度
Buffer b6 = Buffer.buffer(new byte[]{1, 2, 3});
System.out.println(b6.length()); // 3
Buffer b7 = Buffer.buffer("学习");
System.out.println(b7.length()); // 6，为什么不是2呢？因为在UTF-8编码中，每个汉字占3个字节
Buffer b8 = Buffer.buffer("😄");
System.out.println(b8.length()); // 4 ，因为😄被一个特殊的Unicode编码标识，四个字节
```



## 拷贝Buffer

可使用 `copy` 方法创建一个Buffer的副本。

```java
// 拷贝Buffer
Buffer b9 = Buffer.buffer("123");
Buffer b10 = b9.copy();
System.out.println(b10); // 123
```



### 裁剪Buffer

裁剪得到的Buffer是完全依赖于原始Buffer的一个新的Buffer，换句话说，它不会对Buffer中的数据做拷贝。 使用 `slice` 方法裁剪一个Buffer。

```java
// 裁剪
Buffer b11 = Buffer.buffer("😄");
Buffer b12 = b11.slice();
System.out.println(b12 == b11); // false
//
Buffer slice = b11.slice(0, 2);
System.out.println(slice); // � 按照字节截取，所以乱码了
```



# 编写 TCP 服务端和客户端

## 服务端

Vertx实例对象提供了`createNetServer()`方法，可用其创建TCP服务，同时可以使用`NetServerOptions`来设置配置信息，`createNetServer()`方法会返回`NetServer `类型的实例，通过调用该实例的`listen`方法即可创建一个TCP服务端。

```java
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
        netServer.connectHandler(event -> System.out.println("有请求" ));
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
```

运行效果如下：

![服务端](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171438059.png)



## 客户端

客户端与服务端基本一致，只是类名不同。

```java
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
```

这上面的`56893`是TCPServer的端口，控制台输出如下：

![客户端](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171446788.png)



## 总结

TCP服务器中内容很多，API也很多，比如客户端服务器端设置的参数也很多，或者SSL等等，这里并未一一讲解，仅作为入门简介。以后用到的时候慢慢积累吧，一口吃不成一个胖子。



# 编写 HTTP服务端和客户端

## 服务端

他的API设计跟TCP的基本相同，不做详细解释，直接上代码

```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;

public class HTTPServerTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServerOptions options = new HttpServerOptions();
        // 网络活动日志
        options.setLogActivity(true);
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {
            String path = request.path();
            HttpMethod method = request.method();
            System.out.println("请求path=" + path + "，请求Method=" + method);
            HttpServerResponse response = request.response();
            if (path.equals("/users/1")) {
                response.end("user1");
            } else if (path.equals("/users/2")) {
                response.end("user2");
            } else {
                response.end("unknown");
            }
        });
        httpServer.listen(8080);
    }
}
```

代码中定义了两个path，`/users/1`和`/users/2`，端口是`8080`，接下来分别使用`httpie`请求下看看效果。

```http
http http://localhost:8080/users/1
HTTP/1.1 200 OK
content-length: 5

user1


http http://localhost:8080/users/2
HTTP/1.1 200 OK
content-length: 5

user2
```

可以看到能够正确响应。



>有什么问题吗？没有路由吗？这样去判断path好麻烦，有没有已经造好的轮子，我坐上面颠就好了😄？Vert.x Core中好像是没有的，Web模块有，后面学习吧。



### 请求Header

handler中的事件（event），其实就是一个`HttpServerRequest`，通过该实例对象，可以获取header信息。

```java
request.getHeader("header名")
```

### 获取参数

比如获取/users?id=1中的id参数就可以使用如下代码：

```java
request.getParam("id")
```

如果是`body`参数，可以使用bodyHandler处理

```java
request.bodyHandler(totalBuffer -> {
  System.out.println("Full body received, length = " + totalBuffer.length());
});
```

### 处理HTML表单

```java
server.requestHandler(request -> {
  request.setExpectMultipart(true);
  request.endHandler(v -> {
    // The body has now been fully read, so retrieve the form attributes
    MultiMap formAttributes = request.formAttributes();
  });
});
```

### 处理文件上传

```java
server.requestHandler(request -> {
  request.setExpectMultipart(true);
  request.uploadHandler(upload -> {
    System.out.println("Got a file upload " + upload.name());
  });
});
```



## 客户端

```java
HttpClient client = vertx.createHttpClient();
```



# SharedData API

顾名思义，`共享数据（SharedData）` API允许您在如下组件中安全地共享数据：

- 应用程序的不同部分之间
- 同一 Vert.x 实例中的不同应用程序之间
- Vert.x 集群中的不同实例之间

## Local maps

`Local maps` 允许您在同一个 Vert.x 实例中的不同事件循环（如不同的 verticle）之间安全地共享数据。

仅允许将某些数据类型作为键值和值：

* 不可变的类型：比如字符串、布尔类型等。
* 实现了 `Shareable` 接口的类型 （比如Buffer，JSON数组，JSON对象，或您编写的Shareable实现类）。

```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;

public class SharedDataLocalMapsTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        LocalMap<Integer, Integer> localMap = vertx.sharedData().getLocalMap("map1");
        localMap.put(1, 1);

        // 这个localMap就能够在一个程序中的其他处获取并使用
        LocalMap<Integer, Integer> localMap2 = vertx.sharedData().getLocalMap("map1");
        System.out.println(localMap2.get(1)); // 1

        // 如果再创建一个Vertx实例则无法获取到值
        Vertx vertx2 = Vertx.vertx();
        LocalMap<Integer, Integer> localMap3 = vertx2.sharedData().getLocalMap("map1");
        System.out.println(localMap3.get(1)); // null
    }
}
```

从上面例子的打印结果可以看出，同一个Vertx实例的时候，确实能够正确存取数据，但是不同的Vertx确实不能通过`Local Maps`共享数据。



存放数据都有对应的同步异步方法。



## 异步锁

这里的异步锁是一个独占锁。

```java
package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;

public class AsynchronousLockTest extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        SharedData sharedData = vertx.sharedData();
        sharedData.getLock("lock1", res -> {
            if (res.succeeded()) {
                // 获得锁
                Lock lock = res.result();

                // 5秒后我们释放该锁以便其他人可以得到它
                System.out.println(Thread.currentThread().getName() + " 开始执行");
                vertx.setTimer(1000, tid -> lock.release());
                System.out.println(Thread.currentThread().getName() + " 执行完毕");
            } else {
                // 发生错误
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(AsynchronousLockTest.class, new DeploymentOptions().setInstances(3));
    }
}
```

执行结果如下：

![异步锁](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171545780.png)

可以看到每个`Verticle`都是顺序执行的。



如果我不使用锁，则执行顺序不能保证，你看如下代码：

```java
package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;

public class AsynchronousLockTest extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        System.out.println(Thread.currentThread().getName() + " 开始执行");
        System.out.println(Thread.currentThread().getName() + " 执行完毕");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(AsynchronousLockTest.class, new DeploymentOptions().setInstances(3));
    }
}
```

效果图如下：

![无锁](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171547140.png)



可以为锁设置一个超时时间，若获取锁超时，则会通知处理器获取锁失败：

```java
SharedData sharedData = vertx.sharedData();

sharedData.getLockWithTimeout("mylock", 10000, res -> {
  if (res.succeeded()) {
    // 获得锁
    Lock lock = res.result();

  } else {
    // 获取锁失败
  }
});
```



## 分布式锁

`sharedData.getLock()`就能够实现分布式的效果。

创建两个类（两个Main对应两个JVM），一个叫做`DistributedLockTest`，另一个叫做`DistributedLockTest2`。

**DistributedLockTest**

```java
package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;

public class DistributedLockTest1 extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        SharedData sharedData = vertx.sharedData();
        sharedData.getLock("lock1", res -> {
            if (res.succeeded()) {
                // 获得锁
                Lock lock = res.result();

                // 5秒后我们释放该锁以便其他人可以得到它
                System.out.println(Thread.currentThread().getName() + " 开始执行");
                vertx.setTimer(100000, tid -> {
                    lock.release();
                    System.out.println(Thread.currentThread().getName() + " 执行完毕");
                });

            } else {
                // 发生错误
            }
        });
    }

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), vertxAsyncResult -> {
            if (vertxAsyncResult.succeeded()) {
                Vertx vertx = vertxAsyncResult.result();
                vertx.deployVerticle(new DistributedLockTest1());
            } else {
                System.out.println("Failed: " + vertxAsyncResult.cause());
            }
        });
    }
}

```

**DistributedLockTest2**

```java
package cn.programtalk.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;

public class DistributedLockTest2 extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        SharedData sharedData = vertx.sharedData();
        sharedData.getLock("lock1", res -> {
            if (res.succeeded()) {
                // 获得锁
                Lock lock = res.result();
                // 5秒后我们释放该锁以便其他人可以得到它
                System.out.println(Thread.currentThread().getName() + " 开始执行");
                System.out.println(Thread.currentThread().getName() + " 执行完毕");
                lock.release();
            } else {
                // 发生错误
            }
        });
    }

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), vertxAsyncResult -> {
            if (vertxAsyncResult.succeeded()) {
                Vertx vertx = vertxAsyncResult.result();
                vertx.deployVerticle(new DistributedLockTest2());
            } else {
                System.out.println("Failed: " + vertxAsyncResult.cause());
            }
        });
    }
}
```



在`DistributedLockTest1`我设置了很久的延时（为了测试不让其释放锁，我延时了30秒），`DistributedLockTest2`中并没有延时，这样`DistributedLockTest2`中如果获得锁会很快执行完成，现在我先启动`DistributedLockTest1`，再启动`DistributedLockTest2`。



来看下运行结果，启动`DistributedLockTest1`后，紧接着我再去启动`DistributedLockTest2`（要在`DistributedLockTest1`启动30秒内启动`DistributedLockTest2`，否则不能很好的看到效果）：

`DistributedLockTest1`会打印`开始执行`后一直阻塞着，就像下图一样。

![image-20230217160613497](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171606700.png)



在次期间，`DistributedLockTest2`也会一直等待（无法获取锁），30秒之后，`DistributedLockTest1`会打印`执行完毕`，此时`DistributedLockTest2`获取到了锁，也执行成功了。

`DistributedLockTest1`30秒后的效果图：

![DistributedLockTest1-30秒后的效果图](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171616345.png)



`DistributedLockTest2`30秒后的效果图：

![DistributedLockTest2-30秒后的效果图](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302171616905.png)





> 温馨提示：集群模式下，要添加Clusting模块依赖。



## 本地锁

上面获取锁的方式都是`sharedData.getLock()`，该方法获得锁可以与其他节点共享锁。如果只想要一个仅限本地的锁，可以使用`getLocalLock`。

```java
SharedData sharedData = vertx.sharedData();

sharedData.getLocalLock("mylock", res -> {
  if (res.succeeded()) {
    // 仅限本地的计数器
    Lock lock = res.result();

    // 5秒后我们释放该锁以便其他人可以得到它

    vertx.setTimer(5000, tid -> lock.release());

  } else {
    // 发生错误
  }
});
```



## 异步计数器

有时你会需要在本地或者在应用节点之间维护一个原子计数器。

您可以用 `Counter` 来做到这一点。

您可以通过 `getCounter` 方法获取一个实例：

```java
SharedData sharedData = vertx.sharedData();

sharedData.getCounter("mycounter", res -> {
  if (res.succeeded()) {
    Counter counter = res.result();
  } else {
    // 发生错误
  }
});
```

# 文件系统

文件系统主要使用`FileSystem`对象来操作。

## 获取FileSystem对象

通过如下代码可以获取。

```java
package cn.programtalk.vertx.core;

import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

public class FileSystemTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        // 获取文件系统
        FileSystem fileSystem = vertx.fileSystem();
    }
}
```

## 拷贝文件

Vertx的很多Api都是分为同步阻塞和异步非阻塞的，文件拷贝也不例外。

```java
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
```

## 读文件

```java
// 读文件
fileSystem.readFile("input/from.txt", event -> {
    if (event.succeeded()) {
        System.out.println(event.result().toString("UTF-8")); //content
    }
});
```

## 写文件

```java
 // 写文件异步
fileSystem.writeFile("input/from.txt", Buffer.buffer("新写入的内容"), event -> {
    if (event.succeeded()) {
        System.out.println("写入成功");
    }
});
// 写文件异步
fileSystem.writeFileBlocking("input/from.txt", Buffer.buffer("新写入的内容"));
```

## 检查文件是否存在

```java
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
```


# Vertx Web
# Web组件是什么？
Vertx Web一款用于编写复杂的现代Web应用以及和HTTP微服务的工具包。
# 创建项目
使用Web组件需要引入相关依赖，我使用的是Maven项目。
```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-web</artifactId>
 <version>4.3.8</version>
</dependency>
```
# 路由
Vertx已经提供了HTTP服务的相关功能，但是比较Low Level。Web提供了路由功能，对于API的开发尤为重要。
## 创建路由器
路由器通过Router类创建
```java
Router router = Router.router(vertx);
```
有了路由器之后就可以通过器route方法创建不同路径的路由，接下来通过实现一个基本的HTTPServer + Route功能来演示下：
```java
package cn.programtalk.vertx.web;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class RouterTest {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(ctx -> {

            // 所有的请求都会调用这个处理器处理
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");

            // 写入响应并结束处理
            response.end("Hello World from Vert.x-Web!");
        });

        server.requestHandler(router).listen(8080);
    }
}
```
![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202303011204227.png)

## 分层处理器
一个路由可以指定多个Handler，层级处理最后返回。
```java
package cn.programtalk.vertx.web;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class RouterTest {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        Route route = router.route();
        route.handler(ctx -> { // ①

            // 所有的请求都会调用这个处理器处理
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.setChunked(true);
            response.write("route1\n");
            // 此处不结束响应，而是继续执行以后的handler
            ctx.vertx().setTimer(5000, event -> {
                ctx.next();
            });
        });

        // 在定义一个handler
        route.handler(ctx -> { // ②

            // 所有的请求都会调用这个处理器处理
            HttpServerResponse response = ctx.response();
            // 写入响应并结束处理
            response.end("route2");
        });
        server.requestHandler(router).listen(8080);
    }
}
```
在上面的代码中我通过`response.setChunked(true);`设置了分块处理，然后在①和②两处创建了两个Handler。分别设置了不同的返回值。然后通过在第一个handler中使用`ctx.next();`来告知程序不要马上响应请求，去执行下一个Handler。

执行结果如下：
![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202303011307294.png)

可以看到两个Handler写的值，都被response响应给了请求者。

## 简单响应
Handlers 功能强大，可以自定义很多内容，为了使用方便，它也提供了简单快捷的API。
该快捷响应API有如下特点：
* 响应返回JSON。

* 如果处理过程中发生错误，一个适当的错误会返回。

* 如果序列化JSON中发生错误，一个适当的错误会返回。

```java
package cn.programtalk.vertx.web;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class SimpleResponseTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router
                .get("/v1/users")
                // 这个处理器将保证这个响应会被序列化成json
                // content type被设置成 "application/json"
                .respond(
                        ctx -> Future.succeededFuture(new JsonObject().put("hello", "world")));
        router
                .get("/pojo")
                // 这个处理器将保证这个响应会被序列化成json
                // content type被设置成 "application/json"
                .respond(
                        ctx -> Future.succeededFuture(List.of(new User(1, "张飞"))));
        server.requestHandler(router).listen(8080);
    }
}
```
**使用POJO需要引入Jackson依赖**
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

运行后，请求结果如下图：
![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202303011316571.png)

请求POJO的接口：
![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202303011323824.png)

上面中使用的`.get("/v1/users")`就是get请求，也支持post、put等请求方式，这里不一一演示了。

## 阻塞Handler处理器
```java
router.route().blockingHandler(ctx -> {

  // 执行某些同步的耗时操作
  service.doSomethingThatBlocks();

  // 调用下一个处理器
  ctx.next();

});
```
## 精确路由
上面pojo的例子，代码中路由路径设置的是`/pojo`，注意最后没有`/`。那么他就是一个非精确路由，也就是说请求路径pojo后有没有`/`或者有一个或者多个`/`都会被匹配到。

那么什么是精确路由呢？就是在路径后面加上`/`，加上后请求`/pojo`则不会匹配到。

![精确路由](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202303011340998.png)



运行后，请求结果如下：

![/pojo无法请求到](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202303011339494.png)



请求地址最后加上`/`就能正确响应。

![/pojo/](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202303011339963.png)



后面有多个`/`也是没有问题的。

![image-20230301133928437](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202303011339100.png)



## 请求参数

### 查询参数

查询参数通过`queryParam`来获取，只能获取路径中`?`后面的有效参数。

```java
package cn.programtalk.vertx.web;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.List;

public class Route_ParamTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        // 查询参数
        router
                .get("/queryParam") // 路径参数通过
                .respond(
                        ctx -> {
                            // 通过queryParam方法执行key，获取参数值
                            List<String> queryParam = ctx.queryParam("id");
                            return Future.succeededFuture(queryParam);
                        });
        server.requestHandler(router).listen(8080);
    }
}
```

![通过queryParam获取查询参数](E:/github/vertx-tutorials/web/assets/image-20230301135037390.png)



可以看到路径中的id参数成功获取到。

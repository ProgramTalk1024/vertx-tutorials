> 面向Spring编程已经多年，形成了很多的固定思维，似乎离开Spring不会编程一样，并> 且我个人比较喜欢尝试新的技术，浏览网站的时候无意间看到了`Eclipse Vert.x™`和`Quarkus`，简单看了下官网后就被`Eclipse Vert.x™`所吸引了，此时此刻就想尝试下，所以就想系统的学习一下，出个简单的基础教程，这也就是系列教程出现的原因！
 
> 百度搜索了下`Eclipse Vert.x™`的相关文章，说实在的，资料真的很少，官网又是英文文档，阅读有些困难，这对于很多人来说也是一个难点，我的英文阅读能力尚可，所以就一边看官网，一边学习，一边记笔记，对于那些英语不好的学生可以去看[中文站](https://vertx-china.github.io/)。

Eclipse Vert.x™ 是什么？

![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302151911881.png)

[`Vert.x`](https://vertx.io/)官网有一段醒目的文字“Eclipse Vert.x™ Reactive applications on the JVM”，意思就是说Eclipse Vert.x™ 可以构建基于JVM的响应式应用。

`Vert.x`底层使用`Netty`,`Netty` 是由 `JBOSS` 提供的一个 `Java` 开源框架。`Netty` 提供异步的、基于事件驱动的网络应用程序框架，用以快速开发高性能、高可靠性的网络 `IO` 程序,是目前最流行的 `NIO` 框架。

# Eclipse Vert.x™ 的特点
* 轻量小巧：对比Spring这种大而全的框架，Vert.x则是非常轻量，核心包据说不到1M。
* 异步非阻塞：Vert.x使用Netty作为底层支持，它是异步。
* 事件驱动：Vert.x中的每个”操作“都被认为是一个事件，底层通过`Event Loop Thread`或者`Worker Thread Pool`来监听事件。
* 模块化：Vert的提供了各种模块化功能，比如Web等。
* 高并发：Vert.x是一个事件驱动非阻塞的异步编程框架，可以在极少的核心线程里占用最小限度的硬件资源处理大量的高并发请求。
* Vert.x 是一个工具包，而不是一个框架。

# Eclipse Vert.x™ 的缺点
国内不活跃，资料较少，据说国内用的也不是特别多。
![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161313970.png)

看官网的使用公司，也没发现国内公司使用，当然肯定是有人用的。

# Eclipse Vert.x™模块
Vert.x提供了很多的模块共使用者自由选择，比如Core、Web、集群等模块。
![](https://programtalk-1256529903.cos.ap-beijing.myqcloud.com/202302161317556.png)
加下来我就会根据上面官网文档所示图中的内容逐步学习并记录。

# 教程说明
本文章中`Vert.x`所使用的版本是V4.3.8，此版本要求JDK8+，包管理工具我使用Maven3+。

---

> 我会（我相信会）持续更新的，期待您的关注，共同交流学习！

# Core模块
Vert.x 核心 API 包含用于编写 Vert.x 应用程序的核心和对 HTTP、TCP、UDP、文件系统的低级别支持， 异步流和许多其他构建基块。它也用于 Vert.x 的许多其他组件。

下方是Core模块的使用教程。
* [Vert.X Core教程](core/README.md)
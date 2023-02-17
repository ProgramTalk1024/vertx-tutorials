package cn.programtalk.vertx.core;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import io.vertx.core.json.pointer.JsonPointerIterator;
import io.vertx.core.json.pointer.impl.JsonPointerIteratorImpl;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JsonObjectTest {
    public static void main(String[] args) throws URISyntaxException {
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


        // 编码
        // encode将对象编码为字符串
        String encode = jo1.encode();
        System.out.println(encode); // {"id":2,"name":"成吉思汗","sex":"男"}

        // encodePrettilym美观的格式化字符串
        String encodePrettily = jo1.encodePrettily();
        System.out.println(encodePrettily);
        //{
        //  "id" : 2,
        //  "name" : "成吉思汗",
        //  "sex" : "男"
        //}



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

        // 验证字符串是否合法,使用Json.decodeValue方法
        String arbitraryJson = "{\"id\":1}";
        Object object = Json.decodeValue(arbitraryJson);
        if (object instanceof JsonObject) {
            System.out.println("JsonObject"); // 打印这里
        } else if (object instanceof JsonArray) {
            System.out.println("JsonArray");
        } else if (object instanceof String) {
            System.out.println("String");
        } else {
            System.out.println("其他");
        }
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

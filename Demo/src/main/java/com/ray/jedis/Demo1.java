package com.ray.jedis;

import redis.clients.jedis.Jedis;

public class Demo1 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        //设置访问密码
        jedis.auth("123");
        //基础操作
      //  jedis.set("hello","redis");
        String name = jedis.get("hello");
        System.out.println(name);
        Boolean hello = jedis.exists("hello");
        System.out.println(hello);
        String ping = jedis.ping();
        System.out.println(ping);



        Long ttl = jedis.ttl("hello");
        System.out.println(ttl);
    }
}

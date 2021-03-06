package com.ray.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Demo2 {
    public static void main(String[] args) {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(60);
        config.setTestOnBorrow(true);
        config.setMaxActive(100);
        JedisPool jedisPool = new JedisPool(config,"127.0.0.1",6379,60,"123");
        Jedis jedis = jedisPool.getResource();
        jedis.set("book","qweqwe");
        jedis.expire("book",20);
        jedisPool.returnResource(jedis);

    }
}

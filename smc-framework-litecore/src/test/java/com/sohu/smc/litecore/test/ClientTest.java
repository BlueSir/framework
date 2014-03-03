package com.sohu.smc.litecore.test;

import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-12-13
 * Time: 下午2:09
 * To change this template use File | Settings | File Templates.
 */
public class ClientTest {
    public static void main(String... args) throws IOException {

        MemcachedClient client = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
        System.out.println(client.getStats());

        client.add("abc", 1000, "sldfjasldf");

//        client.shutdown();
    }
}

package com.sohu.smc.litecore.test;

import com.sohu.smc.litecore.HandlerFunction;
import com.sohu.smc.litecore.memcached.MemcachedService;
import com.twitter.util.Duration;
import scala.Option;

/**
 * Created with IntelliJ IDEA.
 * User: Qinqd
 * Date: 12-11-16
 * Time: 下午5:51
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {
        int port = 11211;

        MemcachedService.mkServer("0.0.0.0", port, Option.apply((Duration) null), 100,
                new HandlerFunction() {

                    @Override
                    public byte[] get(String key) {
                        return new byte[0];
                    }

                    @Override
                    public boolean set(String key, int flag, int expire, byte[] data) {
                        return false;
                    }

                    @Override
                    public boolean add(String key, int flag, int expire, byte[] data) {

                        System.out.println(key + "||" + flag + "||" + expire + "||" + new String(data));

                        return true;
                    }

                    @Override
                    public String[] stats() {
//                        String[] str = {"push_count 222", "push_ack 220"};
                        return null;
                    }
                });
    }
}

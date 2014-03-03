package com.sohu.smc.common.test;

import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-12-29
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public class ArchaiusTest {

    public static void main(String... args) throws InterruptedException {
        DynamicLongProperty timeToWait = DynamicPropertyFactory.getInstance().getLongProperty("lock.waitTime", 10);

        System.out.println(timeToWait.get());
    }

}

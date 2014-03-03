package com.sohu.smc.common.worker.task;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-9-12
 * Time: 下午12:22
 * To change this template use File | Settings | File Templates.
 */
public class AbstractThread extends Thread {
    private final String echo = "alive-[%s]-" + Thread.currentThread();

    public String ping() {
        return String.format(echo, new Date());
    }
}

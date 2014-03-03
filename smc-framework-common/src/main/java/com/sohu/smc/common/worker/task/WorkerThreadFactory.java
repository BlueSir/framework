package com.sohu.smc.common.worker.task;

import java.util.concurrent.ThreadFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-5-25
 * Time: 下午5:51
 */
public class WorkerThreadFactory implements ThreadFactory {
    private String prefix = "";
    private boolean isDaemon = true;
    private ThreadGroup threadGroup;
    private final static String fm = "WorkerThread-%s-%s";

    public WorkerThreadFactory() {
        SecurityManager sm = System.getSecurityManager();
        this.threadGroup = (sm == null) ? Thread.currentThread().getThreadGroup() : sm.getThreadGroup();
    }

    public WorkerThreadFactory(String _prefix, boolean daemon) {

        if (_prefix == null) {
            this.prefix = Thread.currentThread().getName();
        } else {
            this.prefix = _prefix;
        }

        this.isDaemon = daemon;
        SecurityManager sm = System.getSecurityManager();
        this.threadGroup = (sm == null) ? Thread.currentThread().getThreadGroup() : sm.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread ret = new Thread(threadGroup, r, String.format(fm, prefix, Thread.currentThread().getId()), 0);
        ret.setDaemon(isDaemon);
        return ret;
    }
}

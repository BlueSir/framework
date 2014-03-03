package com.sohu.smc.common.lifecycle;

import com.google.common.base.Function;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: shijinkui
 * Date: 12-7-24
 * Time: 下午3:09
 * To change this template use File | Settings | File Templates.
 */
public class ConsumeTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(ConsumeTask.class.getName());

    private MemcachedClient queue;
    private String queueName;
    private Function<String, Boolean> taskHandler;
    private final String threadName = Thread.currentThread().getName();
    private final int MIN_DELAY_MS = 10;
    private final int MAX_BACKOFF_DELAY_MS = 8192;
    private int sleep_time = MIN_DELAY_MS;


    public ConsumeTask(MemcachedClient q, String queueName, Function<String, Boolean> handler) {
        this.queue = q;
        this.queueName = queueName;
        this.taskHandler = handler;

        logger.info("start a new Schedule Task to consume the queue.");
    }

    @Override
    public void run() {

        boolean isSleep = false;
        Object task = null;
        while (true) {

            if (isSleep) {
                try {
//                    logger.info(threadName + " have sleep " + sleep_time + " ms");
                    Thread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            task = queue.get(queueName);

            if (task != null && task instanceof String) {
                taskHandler.apply((String) task);
                logger.info(threadName + " dequeue, and handle a task.");
            } else {
                sleep_time = Math.min(sleep_time * 2, MAX_BACKOFF_DELAY_MS);
                isSleep = true;
            }
        }
    }
}

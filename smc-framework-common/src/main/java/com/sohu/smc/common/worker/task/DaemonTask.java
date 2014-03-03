package com.sohu.smc.common.worker.task;

import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;

/**
 * 单个task死循环
 * User: shijinkui
 * Date: 12-8-29
 * Time: 下午3:15
 */
@ThreadSafe
public final class DaemonTask<F, T> extends AbstractThread {
    private final Logger logger = LoggerFactory.getLogger(DaemonTask.class);

    private final long periodTime;
    private final Function function;

    public DaemonTask(long periodTime, Function<F, T> fun) {
        this.periodTime = periodTime;
        this.function = fun;
    }

    public DaemonTask(Function<F, T> fun) {
        this.periodTime = -1l;
        this.function = fun;
    }

    /**
     * exeucute bussness method
     */
    @Override
    public void run() {
        logger.info("start thread task...");
        while (true) {
            //apply 为独立的方法，尽量不要使用外部引用
            function.apply(null);
            if (periodTime > 0) {
                try {
                    Thread.sleep(periodTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

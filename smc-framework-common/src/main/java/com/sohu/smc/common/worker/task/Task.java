package com.sohu.smc.common.worker.task;

import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: shijinkui
 * Date: 12-8-29
 * Time: 下午3:15
 */
public final class Task<F, T> extends AbstractThread {
    private final Logger logger = LoggerFactory.getLogger(Task.class);

    private final String echo = "alive";
    private final Function function;
    private final F f;

    public Task(Function<F, T> fun, F f) {
        this.function = fun;
        this.f = f;
    }

    /**
     * exeucute bussness method
     */
    @Override
    public void run() {
        logger.info("start thread task...");
        //apply 为独立的方法，尽量不要使用外部引用
        function.apply(f);
    }
}

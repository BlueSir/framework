package com.sohu.smc.worker;

import com.google.common.base.Function;
import com.sohu.smc.common.worker.AbstractWorker;
import junit.framework.TestCase;

import javax.annotation.Nullable;

/**
 * User: shijinkui
 * Date: 12-8-29
 * Time: 下午5:27
 * To change this template use File | Settings | File Templates.
 */
public class WorkerTest extends TestCase {
    private AbstractWorker worker;

    public void setUp() {
        worker = new AbstractWorker("testworker", 9999, 2, 2);
        System.out.println("aaaaa");
    }

    public void test_task() {

        worker.start(new Function() {
            @Override
            public Object apply(@Nullable Object input) {
                System.out.println(this + ", im running " + System.nanoTime());
                return null;
            }
        });
    }
}

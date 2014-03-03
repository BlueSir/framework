package com.sohu.smc.admin;

import com.twitter.ostrich.admin.AdminHttpService;
import com.twitter.ostrich.admin.RuntimeEnvironment;
import com.twitter.ostrich.admin.TimeSeriesCollectorFactory;
import com.twitter.ostrich.stats.Stats;

/**
 * Created by IntelliJ IDEA.
 * User: qinqd
 * Date: 12-4-11
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public void start() {


        RuntimeEnvironment runtime = new RuntimeEnvironment(this);
        AdminHttpService admin = new AdminHttpService(9090, 123, runtime);
        TimeSeriesCollectorFactory seriesCollectorFactory = new TimeSeriesCollectorFactory();
        seriesCollectorFactory.apply(Stats.get(""), admin);
        admin.start();

        Stats.addMetric("metirc", 1);
        Stats.incr("abc", 100);

//        Stats.addGauge("connections", 222);
    }

    //  http://127.0.0.1:9090/graph/
    public static void main(String args[]) {
        Test t = new Test();
        t.start();
    }
}
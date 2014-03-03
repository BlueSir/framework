package com.sohu.smc.sample.resource;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.sohu.smc.core.annotation.RequestMapping;
import com.sohu.smc.core.server.ActionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: zhangsuozhu
 * Date: 13-1-17
 * Time: 下午1:27
 */
@RequestMapping("stats")
public class StatsAction {
    private static final Logger log = LoggerFactory.getLogger(StatsAction.class);
    @RequestMapping
    public  String stats(){
        HystrixCommandMetrics orderMetrics = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(ActionCommand.class.getSimpleName()));

        // print out metrics
        StringBuilder out = new StringBuilder();
        out.append("\n");

        out.append("ActionCommand: " + getStatsStringFromMetrics(orderMetrics)).append("\n");

       return (out.toString());
    }

    private String getStatsStringFromMetrics(HystrixCommandMetrics metrics) {
        StringBuilder m = new StringBuilder();
        if (metrics != null) {
            HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
            m.append("Requests: ").append(health.getTotalRequests()).append(" ");
            m.append("Errors: ").append(health.getErrorCount()).append(" (").append(health.getErrorPercentage()).append("%)   ");
            m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
            m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
            m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
            m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
        }
        return m.toString();
    }
}

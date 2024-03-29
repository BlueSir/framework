package com.sohu.smc.core.metric;

import com.netflix.hystrix.*;
import com.netflix.hystrix.util.HystrixRollingNumberEvent;
import org.codehaus.jackson.JsonGenerator;

import static com.sohu.smc.core.metric.MetricType.hystrix;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-30
 * Time: 上午10:47
 * To change this template use File | Settings | File Templates.
 */
public class HystrixMetricsTask extends AbstractTask {

    public HystrixMetricsTask(String instanceName, String ip, int port) {
        super(instanceName, ip, port);
    }

    @Override
    public void run() {
        try {
            // command metrics
            for (HystrixCommandMetrics commandMetrics : HystrixCommandMetrics.getInstances()) {
                HystrixCommandKey key = commandMetrics.getCommandKey();
                HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.Factory.getInstance(key);

                JsonGenerator json = getJson();

                json.writeStringField("type", "HystrixCommand");
                json.writeStringField("name", key.name());
                json.writeStringField("group", commandMetrics.getCommandGroup().name());
                json.writeNumberField("currentTime", System.currentTimeMillis());

                // circuit breaker
                json.writeBooleanField("isCircuitBreakerOpen", circuitBreaker.isOpen());
                HystrixCommandMetrics.HealthCounts healthCounts = commandMetrics.getHealthCounts();
                json.writeNumberField("errorPercentage", healthCounts.getErrorPercentage());
                json.writeNumberField("errorCount", healthCounts.getErrorCount());
                json.writeNumberField("requestCount", healthCounts.getTotalRequests());

                // rolling counters
                json.writeNumberField("rollingCountCollapsedRequests", commandMetrics.getRollingCount(HystrixRollingNumberEvent.COLLAPSED));
                json.writeNumberField("rollingCountExceptionsThrown", commandMetrics.getRollingCount(HystrixRollingNumberEvent.EXCEPTION_THROWN));
                json.writeNumberField("rollingCountFailure", commandMetrics.getRollingCount(HystrixRollingNumberEvent.FAILURE));
                json.writeNumberField("rollingCountFallbackFailure", commandMetrics.getRollingCount(HystrixRollingNumberEvent.FALLBACK_FAILURE));
                json.writeNumberField("rollingCountFallbackRejection", commandMetrics.getRollingCount(HystrixRollingNumberEvent.FALLBACK_REJECTION));
                json.writeNumberField("rollingCountFallbackSuccess", commandMetrics.getRollingCount(HystrixRollingNumberEvent.FALLBACK_SUCCESS));
                json.writeNumberField("rollingCountResponsesFromCache", commandMetrics.getRollingCount(HystrixRollingNumberEvent.RESPONSE_FROM_CACHE));
                json.writeNumberField("rollingCountSemaphoreRejected", commandMetrics.getRollingCount(HystrixRollingNumberEvent.SEMAPHORE_REJECTED));
                json.writeNumberField("rollingCountShortCircuited", commandMetrics.getRollingCount(HystrixRollingNumberEvent.SHORT_CIRCUITED));
                json.writeNumberField("rollingCountSuccess", commandMetrics.getRollingCount(HystrixRollingNumberEvent.SUCCESS));
                json.writeNumberField("rollingCountThreadPoolRejected", commandMetrics.getRollingCount(HystrixRollingNumberEvent.THREAD_POOL_REJECTED));
                json.writeNumberField("rollingCountTimeout", commandMetrics.getRollingCount(HystrixRollingNumberEvent.TIMEOUT));

                json.writeNumberField("currentConcurrentExecutionCount", commandMetrics.getCurrentConcurrentExecutionCount());

                // latency percentiles
                json.writeNumberField("latencyExecute_mean", commandMetrics.getExecutionTimeMean());
                json.writeObjectFieldStart("latencyExecute");
                json.writeNumberField("0", commandMetrics.getExecutionTimePercentile(0));
                json.writeNumberField("25", commandMetrics.getExecutionTimePercentile(25));
                json.writeNumberField("50", commandMetrics.getExecutionTimePercentile(50));
                json.writeNumberField("75", commandMetrics.getExecutionTimePercentile(75));
                json.writeNumberField("90", commandMetrics.getExecutionTimePercentile(90));
                json.writeNumberField("95", commandMetrics.getExecutionTimePercentile(95));
                json.writeNumberField("99", commandMetrics.getExecutionTimePercentile(99));
                json.writeNumberField("99.5", commandMetrics.getExecutionTimePercentile(99.5));
                json.writeNumberField("100", commandMetrics.getExecutionTimePercentile(100));
                json.writeEndObject();
                //
                json.writeNumberField("latencyTotal_mean", commandMetrics.getTotalTimeMean());
                json.writeObjectFieldStart("latencyTotal");
                json.writeNumberField("0", commandMetrics.getTotalTimePercentile(0));
                json.writeNumberField("25", commandMetrics.getTotalTimePercentile(25));
                json.writeNumberField("50", commandMetrics.getTotalTimePercentile(50));
                json.writeNumberField("75", commandMetrics.getTotalTimePercentile(75));
                json.writeNumberField("90", commandMetrics.getTotalTimePercentile(90));
                json.writeNumberField("95", commandMetrics.getTotalTimePercentile(95));
                json.writeNumberField("99", commandMetrics.getTotalTimePercentile(99));
                json.writeNumberField("99.5", commandMetrics.getTotalTimePercentile(99.5));
                json.writeNumberField("100", commandMetrics.getTotalTimePercentile(100));
                json.writeEndObject();

                // property values for reporting what is actually seen by the command rather than what was set somewhere
                HystrixCommandProperties commandProperties = commandMetrics.getProperties();

                json.writeNumberField("propertyValue_circuitBreakerRequestVolumeThreshold", commandProperties.circuitBreakerRequestVolumeThreshold().get());
                json.writeNumberField("propertyValue_circuitBreakerSleepWindowInMilliseconds", commandProperties.circuitBreakerSleepWindowInMilliseconds().get());
                json.writeNumberField("propertyValue_circuitBreakerErrorThresholdPercentage", commandProperties.circuitBreakerErrorThresholdPercentage().get());
                json.writeBooleanField("propertyValue_circuitBreakerForceOpen", commandProperties.circuitBreakerForceOpen().get());
                json.writeBooleanField("propertyValue_circuitBreakerForceClosed", commandProperties.circuitBreakerForceClosed().get());
                json.writeBooleanField("propertyValue_circuitBreakerEnabled", commandProperties.circuitBreakerEnabled().get());

                json.writeStringField("propertyValue_executionIsolationStrategy", commandProperties.executionIsolationStrategy().get().name());
                json.writeNumberField("propertyValue_executionIsolationThreadTimeoutInMilliseconds", commandProperties.executionIsolationThreadTimeoutInMilliseconds().get());
                json.writeBooleanField("propertyValue_executionIsolationThreadInterruptOnTimeout", commandProperties.executionIsolationThreadInterruptOnTimeout().get());
                json.writeStringField("propertyValue_executionIsolationThreadPoolKeyOverride", commandProperties.executionIsolationThreadPoolKeyOverride().get());
                json.writeNumberField("propertyValue_executionIsolationSemaphoreMaxConcurrentRequests", commandProperties.executionIsolationSemaphoreMaxConcurrentRequests().get());
                json.writeNumberField("propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests", commandProperties.fallbackIsolationSemaphoreMaxConcurrentRequests().get());

                    /*
                     * The following are commented out as these rarely change and are verbose for streaming for something people don't change.
                     * We could perhaps allow a property or request argument to include these.
                     */

                //                    json.put("propertyValue_metricsRollingPercentileEnabled", commandProperties.metricsRollingPercentileEnabled().get());
                //                    json.put("propertyValue_metricsRollingPercentileBucketSize", commandProperties.metricsRollingPercentileBucketSize().get());
                //                    json.put("propertyValue_metricsRollingPercentileWindow", commandProperties.metricsRollingPercentileWindowInMilliseconds().get());
                //                    json.put("propertyValue_metricsRollingPercentileWindowBuckets", commandProperties.metricsRollingPercentileWindowBuckets().get());
                //                    json.put("propertyValue_metricsRollingStatisticalWindowBuckets", commandProperties.metricsRollingStatisticalWindowBuckets().get());
                json.writeNumberField("propertyValue_metricsRollingStatisticalWindowInMilliseconds", commandProperties.metricsRollingStatisticalWindowInMilliseconds().get());

                json.writeBooleanField("propertyValue_requestCacheEnabled", commandProperties.requestCacheEnabled().get());
                json.writeBooleanField("propertyValue_requestLogEnabled", commandProperties.requestLogEnabled().get());

                json.writeNumberField("reportingHosts", 1); // this will get summed across all instances in a cluster

                send(json);
            }

            // thread pool metrics
            for (HystrixThreadPoolMetrics threadPoolMetrics : HystrixThreadPoolMetrics.getInstances()) {
                HystrixThreadPoolKey key = threadPoolMetrics.getThreadPoolKey();

                JsonGenerator json = getJson();


                json.writeStringField("type", "HystrixThreadPool");
                json.writeStringField("name", key.name());
                json.writeNumberField("currentTime", System.currentTimeMillis());

                json.writeNumberField("currentActiveCount", threadPoolMetrics.getCurrentActiveCount().intValue());
                json.writeNumberField("currentCompletedTaskCount", threadPoolMetrics.getCurrentCompletedTaskCount().longValue());
                json.writeNumberField("currentCorePoolSize", threadPoolMetrics.getCurrentCorePoolSize().intValue());
                json.writeNumberField("currentLargestPoolSize", threadPoolMetrics.getCurrentLargestPoolSize().intValue());
                json.writeNumberField("currentMaximumPoolSize", threadPoolMetrics.getCurrentMaximumPoolSize().intValue());
                json.writeNumberField("currentPoolSize", threadPoolMetrics.getCurrentPoolSize().intValue());
                json.writeNumberField("currentQueueSize", threadPoolMetrics.getCurrentQueueSize().intValue());
                json.writeNumberField("currentTaskCount", threadPoolMetrics.getCurrentTaskCount().longValue());
                json.writeNumberField("rollingCountThreadsExecuted", threadPoolMetrics.getRollingCountThreadsExecuted());
                json.writeNumberField("rollingMaxActiveThreads", threadPoolMetrics.getRollingMaxActiveThreads());

                //json.writeNumberField("propertyValue_queueSizeRejectionThreshold", threadPoolMetrics.getProperties().queueSizeRejectionThreshold().get());
                //json.writeNumberField("propertyValue_metricsRollingStatisticalWindowInMilliseconds", threadPoolMetrics.getProperties().metricsRollingStatisticalWindowInMilliseconds().get());
                send(json);
            }

        } catch (Exception e) {
            System.err.println("Failed to output metrics as JSON");
            e.printStackTrace();
            return;
        }
    }

    @Override
    public MetricType getType() {
        return hystrix;
    }
}

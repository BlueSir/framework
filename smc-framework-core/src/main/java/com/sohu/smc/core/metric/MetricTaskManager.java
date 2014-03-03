package com.sohu.smc.core.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;


public class MetricTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(MetricTaskManager.class);

    private final ScheduledExecutorService executor;
    private final int default_delay = 5;

    private volatile ScheduledFuture<?> scheduledTask = null;

    public MetricTaskManager() {
        executor = new ScheduledThreadPoolExecutor(1, new MetricsPollerThreadFactory());
    }

    public void addTask(Runnable task) {
        addTask(task, default_delay);
    }

    public synchronized void addTask(Runnable task, int delay) {

        int random = (int) (Math.random() * 2000);
        System.out.println(random);
        long delayTime = TimeUnit.SECONDS.toMillis(delay) + random;

        logger.info("Starting task");
        scheduledTask = executor.scheduleWithFixedDelay(task, delayTime, delayTime, TimeUnit.MILLISECONDS);
    }

    public synchronized void pause() {
        logger.info("Stopping the Servo Metrics Poller");
        scheduledTask.cancel(true);
    }

    public synchronized void shutdown() {
        pause();
        executor.shutdown();
    }

    /**
     * Used to protect against leaking ExecutorServices and threads if this class is abandoned for GC without shutting down.
     */
    @SuppressWarnings("unused")
    private final Object finalizerGuardian = new Object() {
        protected void finalize() throws Throwable {
            if (!executor.isShutdown()) {
                logger.warn(MetricTaskManager.class.getSimpleName() + " was not shutdown. Caught in Finalize Guardian and shutting down.");
                try {
                    shutdown();
                } catch (Exception e) {
                    logger.error("Failed to shutdown " + MetricTaskManager.class.getSimpleName(), e);
                }
            }
        }

        ;
    };

    private final class MetricsPollerThreadFactory implements ThreadFactory {
        private static final String threadName = "metrics-task";
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        public Thread newThread(Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName(threadName);
            return thread;
        }
    }
}


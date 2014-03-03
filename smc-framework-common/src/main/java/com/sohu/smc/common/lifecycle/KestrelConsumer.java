package com.sohu.smc.common.lifecycle;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.sohu.smc.common.lifecycle.memcached.Memcached;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for pulling work items off of a kestrel queue.
 * <p/>
 * fixed thread num execute task.
 *
 * @author William Farner
 */
public class KestrelConsumer {
    private static Logger log = LoggerFactory.getLogger(KestrelConsumer.class.getName());
    private MemcachedClient queue;
    private final List<String> kestrelServers;
    private final String queueName;
    private final Function<String, Boolean> taskHandler;
    private ExecutorService executorService;
    private ScheduledExecutorService schedule;
    private final int task_num = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * Creates a new kestrel consumer that will communicate with the given kestrel servers (where
     * a server string is formatted as host:port).
     *
     * @param kestrelServers The kestrel servers to pull work from.
     * @param queueName      The name of the kestrel queue to pull work from.
     * @param taskHandler    The handler for new work retrieved from the kestrel queue. The handler
     *                       should return {@code false} if the work item was not successfully handled.
     */
    public KestrelConsumer(List<String> kestrelServers, String queueName,
                           Function<String, Boolean> taskHandler) {
        Preconditions.checkNotNull(kestrelServers);
        Preconditions.checkNotNull(queueName);
        Preconditions.checkNotNull(taskHandler);
        this.kestrelServers = kestrelServers;
        this.queueName = queueName;
        this.taskHandler = taskHandler;
    }

    public synchronized void initialize() {
        queue = Memcached.newKestrelClient(kestrelServers);
        executorService = Executors.newFixedThreadPool(task_num);
        schedule = Executors.newScheduledThreadPool(kestrelServers.size());
    }

    public void start() {
        Preconditions.checkNotNull(queue);
        Preconditions.checkNotNull(taskHandler);

        log.info("Consuming items from the kestrel queue forever.");

        for (int i = 0; i < task_num; i++) {
            executorService.execute(new ConsumeTask(queue, queueName, taskHandler));
        }
        monitoring();
    }

    private void monitoring() {

        for (final String server : kestrelServers) {
            schedule.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    String ret = null;
                    Map<SocketAddress, Map<String, String>> map = queue.getStats();
                    for (Map.Entry<SocketAddress, Map<String, String>> entry : map.entrySet()) {
                        Map<String, String> values = entry.getValue();
                        ret = values.get("queue_" + queueName + "_items");
                        if (ret != null && Integer.parseInt(ret) > 2000) {
                            //10.13.81.60:9999/sms.php?receiver=159xxx&msg=xxx     todo
                            log.error(entry.getKey() + "[queue_" + queueName + "_items] is over 2000");
                        }
                        continue;
                    }
                }
            }, 10, 10, TimeUnit.SECONDS);
        }
    }
}
package com.sohu.smc.common.worker;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.sohu.smc.common.util.IpUtil;
import com.sohu.smc.common.worker.task.*;
import com.twitter.ostrich.admin.AdminHttpService;
import com.twitter.ostrich.admin.RuntimeEnvironment;
import com.twitter.ostrich.admin.TimeSeriesCollectorFactory;
import com.twitter.ostrich.stats.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sohu.smc.common.worker.task.TaskType.daemon_task;

/**
 * worker
 * User: shijinkui
 * Date: 12-8-29
 * Time: 下午4:13
 */
@ThreadSafe
public class AbstractWorker<F, T> {
    private final Logger log = LoggerFactory.getLogger(AbstractWorker.class.getName());

    private final String workerName;
    private final int adminport;
    private final int tasknum;
    private final long periodTime;
    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    private final ScheduledExecutorService scheduleTask;

    private AbstractThread[] taskList;
    private TaskType taskType;

    public AbstractWorker(String workerName, int adminport, int taskNum, long periodTime) {
        Preconditions.checkNotNull(workerName);
        Preconditions.checkArgument(adminport > 0);
        Preconditions.checkArgument(taskNum > 0);
        Preconditions.checkArgument(periodTime > 0);

        this.workerName = workerName;
        this.adminport = adminport;
        this.tasknum = taskNum;
        this.periodTime = periodTime;
        setTaskType(daemon_task);
        scheduleTask = Executors.newScheduledThreadPool(2, new WorkerThreadFactory(this.workerName, true));
    }

    public final void start(Function<F, T> function) {
        log.info("starting the worker...");
        log.info("starting schedule the worker tasks ...");
        startAdmin();

        taskList = new AbstractThread[tasknum];
        for (int i = 0; tasknum > 0 && i < tasknum; i++) {

            switch (taskType) {
                case base_task:
                    taskList[i] = new Task(function, null);
                    break;
                default:
                case daemon_task:
                    taskList[i] = new DaemonTask(periodTime, function);
                    break;
            }

            taskExecutor.execute(taskList[i]);
        }

        printInfo();
        healthcheck();
    }

    public void healthcheck() {
        //schedue ping
        scheduleTask.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < taskList.length; i++) {
                    AbstractThread task = taskList[i];
                    if (task == null) {
                        continue;
                    }
                    Stats.incr("monitor thread: " + task.getName());
                    try {
                        String ping = task.ping();
                        if (ping == null || ping.length() < 5) {
                            log.warn("task is dead... ");
                        }
                    } catch (Exception e) {
                        log.error("error: task is dead, delete from the list. ", e);
                    }
                }
            }
        }, 10, 60, TimeUnit.SECONDS);
    }

    public void stop() {
        if (!taskExecutor.isShutdown()) {
            taskExecutor.shutdown();
        }

        if (!scheduleTask.isShutdown()) {
            scheduleTask.shutdown();
        }

        log.info("shutdown all the task and schedue task.");
    }

    private void printInfo() {
        //System.out.print(UsagePrinter.getLine(50));
        System.out.print(this.toString());
        //System.out.println(UsagePrinter.getLine(50));
    }

    private void startAdmin() {
        AdminHttpService admin = null;
        try {
            admin = new AdminHttpService(adminport, 123, new RuntimeEnvironment(this));
        } catch (Exception e) {
            log.error("admin http service start err", e);
        }

        TimeSeriesCollectorFactory seriesCollectorFactory = new TimeSeriesCollectorFactory();
        seriesCollectorFactory.apply(Stats.get(""), admin).start();
        assert admin != null;
        admin.start();
    }

    public String toString() {
        String url = "admin monitor url : http://" + IpUtil.getIp() + ":" + adminport + "/stats.txt";
        String graphurl = "admin monitor url : http://" + IpUtil.getIp() + ":" + adminport + "/graph/";
        StringBuffer threads = new StringBuffer("");
        for (AbstractThread task : taskList) {
            threads.append(task.toString()).append("\n");
        }
        return Joiner.on("\n").join("workerName: " + workerName, url, graphurl, "task num: " + tasknum, "tasks:", threads);
    }

    public static void main(String... args) {
        AbstractWorker bw = new AbstractWorker("asdafasdf", 9999, 2, 2);
        bw.start(new Function() {
            @Override
            public Object apply(@Nullable Object input) {
                System.out.println(System.nanoTime() + "----" + Thread.currentThread().getName());
//                Stats.addGauge(Thread.currentThread().getName(), 2);
                Stats.incr(Thread.currentThread().getName());
                return null;
            }
        });
    }

    public ScheduledExecutorService getScheduleTask() {
        return scheduleTask;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
}

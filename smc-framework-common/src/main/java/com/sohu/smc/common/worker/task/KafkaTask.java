package com.sohu.smc.common.worker.task;

import com.google.common.base.Function;
import com.twitter.ostrich.stats.Stats;
import kafka.consumer.KafkaStream;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-9-13
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */
public class KafkaTask<T extends Function<byte[], Boolean>> extends AbstractThread {
    private KafkaStream<Message> stream;
    private final T taskHandler;
    private long counter = 1l;
    private final long gateway = 1000;
    private long ct = System.currentTimeMillis();
    private final String metric = "process-per-second";
    private final SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd_HH");
    private final SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);

    public KafkaTask(T taskHandler) {
        this.taskHandler = taskHandler;
    }


    @Override
    public void run() {
        ByteBuffer buffer = null;
        for (MessageAndMetadata msgAndMetadata : stream) {
            try {
                buffer = ((kafka.message.Message) msgAndMetadata.message()).payload();
                if (buffer == null || !buffer.hasRemaining()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] b = new byte[buffer.remaining()];
            buffer.get(b);
            buffer.clear();
            taskHandler.apply(b);

            //monitor thing
            if (counter++ % gateway == 0) {
//                long s = gateway / ((System.currentTimeMillis() - ct) / 1000);
//                Stats.addMetric(metric, (int) s);
//                ct = System.currentTimeMillis();
//                System.out.println(Thread.currentThread() + "||" + counter + "||count-per-second:" + s + "||" + new String(b));
                System.out.println(Thread.currentThread() + "||" + counter + new String(b));
                record(b, gateway);
            }

        }
    }

    private void record(final byte[] log, long incr) {
        String time = new String(log);
        String str = time.substring(time.indexOf("[") + 1, time.indexOf("]"));
        try {
            Stats.incr(fm.format(format.parse(str).getTime()), (int) incr);
        } catch (ParseException e) {
            System.err.println("parse time err:" + time);
            e.printStackTrace();
        }
    }

    public void setStream(KafkaStream<Message> stream) {
        this.stream = stream;
    }

    public T getTaskHandler() {
        return taskHandler;
    }
}

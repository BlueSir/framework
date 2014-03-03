package com.sohu.smc.common.lifecycle.kafka;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.sohu.smc.common.worker.task.KafkaTask;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.producer.Producer;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * kafka使用入口
 * User: shijinkui
 * Date: 12-8-31
 * Time: 下午3:20
 * To change this template use File | Settings | File Templates.
 */
public class KafkaFactory {
    private final static Logger log = LoggerFactory.getLogger(KafkaFactory.class);

    private static KafkaConfig config;
    private static boolean isloaded = false;
    private final static ConcurrentMap<String, Producer> producer_instance = new ConcurrentHashMap<String, Producer>();
    private final static ConcurrentMap<String, ConsumerConnector> consumer_instance = new ConcurrentHashMap<String, ConsumerConnector>();
    private final static ConcurrentMap<String, SimpleConsumer> simple_consumer_instance = new ConcurrentHashMap<String, SimpleConsumer>();

    static {
        init();
    }

    private synchronized static void init() {
        if (isloaded) {
            log.warn("kafkaContext.xml has been loaded...");
        }
//        final Resource resource = new ClassPathResource("kafkaContext.xml");
//        final BeanFactory ctx = new XmlBeanFactory(resource);
        config = new KafkaConfig();
        isloaded = true;
    }

    public static Producer getProducer(String topic) {

        if (producer_instance.containsKey(topic)) {
            return producer_instance.get(topic);
        }

        Producer producer = new Producer<Integer, String>(config.getProducer());
        producer_instance.put(topic, producer);

        return producer;
    }

    public static ConsumerConnector getConsumer(String topic) {

        if (consumer_instance.containsKey(topic)) {
            return consumer_instance.get(topic);
        }

        ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config.getConsumer(topic));
        consumer_instance.put(topic, consumer);
        return consumer;
    }

    public static ConsumerConnector getConsumer(String topic, String groupid) {

        if (consumer_instance.containsKey(topic)) {
            return consumer_instance.get(topic);
        }

        ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config.getConsumer(topic, groupid));
        consumer_instance.put(topic, consumer);
        return consumer;
    }

    public static SimpleConsumer getSimpleConsumer(String brokeList, String topic) {

        if (simple_consumer_instance.containsKey(topic)) {
            return simple_consumer_instance.get(topic);
        }

        SimpleConsumer simpleConsumer = config.getSimpleConsumer(brokeList, topic);
        simple_consumer_instance.put(topic, simpleConsumer);

        return simpleConsumer;
    }


    /**
     * 按照ip分组
     *
     * @param topic
     * @param threadNum
     * @param taskHandler
     */
    public static void consumeForever(String topic, int threadNum, final Function<byte[], Boolean> taskHandler) {
        consumeForever(topic, null, threadNum, taskHandler);
    }

    /**
     * @param topic
     * @param groupid
     * @param threadNum
     * @param taskHandler
     */
    public static void consumeForever(String topic, String groupid, int threadNum, final Function<byte[], Boolean> taskHandler) {
        log.info("Consuming items from the kafka queue forever.");

        ConsumerConnector connector;
        if (groupid != null) {
            connector = getConsumer(topic, groupid);
        } else {
            connector = getConsumer(topic);
        }

        // create 4 partitions of the stream for topic , to allow {@param threadNum} threads to consume
        final Map<String, List<KafkaStream<Message>>> topicMessageStreams = connector.createMessageStreams(ImmutableMap.of(topic, threadNum));
        final ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        final List<KafkaStream<Message>> streams = topicMessageStreams.get(topic);
        // create list of 4 threads to consume from each of the partitions
//        final AtomicLong atomicLong = new AtomicLong(0);
        // consume the messages in the threads
        for (final KafkaStream<Message> stream : streams) {
            executor.submit(new Runnable() {
                public void run() {

                    for (MessageAndMetadata msgAndMetadata : stream) {
                        byte[] b = getMessage((kafka.message.Message) msgAndMetadata.message());
//                        if (atomicLong.incrementAndGet() % 1000 == 0) {
//                            System.out.println(atomicLong.get() + "||" + new String(b));
//                        }
                        taskHandler.apply(b);
                    }
                }
            });
        }
    }

    public static void consumeForever(String topic, int threadNum, final List<KafkaTask> list) {
        log.info("Consuming items from the kafka queue forever.");
        final Map<String, List<KafkaStream<Message>>> topicMessageStreams = getConsumer(topic).createMessageStreams(ImmutableMap.of(topic, threadNum));
//        final ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        final List<KafkaStream<Message>> streams = topicMessageStreams.get(topic);
//        final AtomicLong atomicLong = new AtomicLong(0);
        // consume the messages in the threads
        for (int i = 0; i < streams.size(); i++) {
            KafkaTask task = list.get(i);
            task.setStream(streams.get(i));
            task.start();
//            executor.submit(task);
        }
    }

    private static byte[] getMessage(Message message) {
        ByteBuffer buffer = message.payload();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }


    public static void main(String... args) {
        final AtomicLong atomicLong = new AtomicLong(0);
        consumeForever("scm-tools", 1, new Function<byte[], Boolean>() {
            @Override
            public Boolean apply(@Nullable byte[] input) {
                if (atomicLong.incrementAndGet() % 1000 == 0) {
                    System.out.println(atomicLong.get() + "||" + new String(input));
                }
                return true;
            }

            @Override
            public boolean equals(@Nullable Object object) {
                return false;
            }
        });
    }

}

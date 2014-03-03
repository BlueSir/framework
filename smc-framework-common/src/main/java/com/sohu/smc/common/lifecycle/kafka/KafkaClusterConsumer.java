//package com.sohu.smc.dropwizard.lifecycle.kafka;
//
//import com.google.common.base.Function;
//import com.google.common.base.Preconditions;
//import com.google.common.collect.ImmutableMap;
//import kafka.api.FetchRequest;
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;
//import kafka.javaapi.consumer.SimpleConsumer;
//import kafka.javaapi.message.ByteBufferMessageSet;
//import kafka.message.Message;
//import kafka.message.MessageAndMetadata;
//import kafka.message.MessageAndOffset;
//import org.apache.commons.io.FileUtils;
//
//import java.io.File;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// Class responsible for pulling work items off of a kestrel queue.
// @author William Farner
// **/
//public class KafkaClusterConsumer {
//    private static Logger log = Logger.getLogger(KafkaClusterConsumer.class.getName());
//    private ConsumerConnector consumerConnector;
//    private final String zkConn;
//    private final String topic;
//    private final Function<byte[], Boolean> taskHandler;
//    // Controls backoffs when there is no work available.
//    private static final int MIN_DELAY_MS = 1;
//    private static final int MAX_BACKOFF_DELAY_MS = 8192;
//    private int backoffDelayMs = MIN_DELAY_MS;
//    // Tracks rate at which items are being removed from the queue.
//    //private final AtomicLong stats = Stats.exportLong("kestrel_consumption");
//    /**
//     * Creates a new kestrel consumer that will communicate with the given kestrel servers (where
//     * a server string is formatted as host:port).
//     *
//     * @param zkConn The kestrel servers to pull work from.
//     * @param topic The name of the kestrel queue to pull work from.
//     * @param taskHandler The handler for new work retrieved from the kestrel queue. The handler
//     *    should return {@code false} if the work item was not successfully handled.
//     */
//    public KafkaClusterConsumer(String zkConn, String topic ,
//                                Function<byte[], Boolean> taskHandler) {
//        Preconditions.checkNotNull(zkConn);
//        Preconditions.checkNotNull(topic);
//        Preconditions.checkNotNull(taskHandler);
//        this.zkConn = zkConn;
//        this.topic = topic;
//        this.taskHandler = taskHandler;
//    }
//    public void initialize() {
//        consumerConnector = KafkaUtil.getConsumer(zkConn, topic);
//    }
//    public void consumeForever() {
//        Preconditions.checkNotNull(consumerConnector);
//        System.out.println("Consuming items from the kafka queue forever.");
//
//        // create 4 partitions of the stream for topic , to allow 4 threads to consume
//        Map<String, List<KafkaStream<Message>>> topicMessageStreams =
//                consumerConnector.createMessageStreams(ImmutableMap.of(topic, 4));
//        List<KafkaStream<Message>> streams = topicMessageStreams.get(topic);
//
//        // create list of 4 threads to consume from each of the partitions
//        ExecutorService executor = Executors.newFixedThreadPool(4);
//
//        final AtomicLong atomicLong = new AtomicLong();
//        // consume the messages in the threads
//        for(final KafkaStream<Message> stream: streams) {
//            executor.submit(new Runnable() {
//                public void run() {
//                    for(MessageAndMetadata msgAndMetadata: stream) {
//                        // process message (msgAndMetadata.message())
//                        if(atomicLong.incrementAndGet() % 10000 == 0)
//                            System.out.println(atomicLong.get() + "\t" + new String(KafkaUtil.getMessage((kafka.message.Message)msgAndMetadata.message())).substring(0,30));
//                        taskHandler.apply(KafkaUtil.getMessage((kafka.message.Message)msgAndMetadata.message()));
//                    }
//                }
//            });
//        }
//    }
//}

//package com.sohu.smc.dropwizard.lifecycle.kafka;
//
//import com.google.common.base.Function;
//import com.google.common.base.Preconditions;
//import kafka.api.FetchRequest;
//import kafka.javaapi.consumer.SimpleConsumer;
//import kafka.javaapi.message.ByteBufferMessageSet;
//import kafka.message.MessageAndOffset;
//import org.apache.commons.io.FileUtils;
//
//import java.io.File;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// Class responsible for pulling work items off of a kestrel queue.
// @author William Farner
// **/
//public class KafkaConsumer {
//    private static Logger log = Logger.getLogger(KafkaConsumer.class.getName());
//    private SimpleConsumer consumerConnector;
//    private final String brokeList;
//    private final String fileOffset;
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
//     * @param brokeList The kestrel servers to pull work from.
//     * @param topic The name of the kestrel queue to pull work from.
//     * @param taskHandler The handler for new work retrieved from the kestrel queue. The handler
//     *    should return {@code false} if the work item was not successfully handled.
//     */
//    public KafkaConsumer(String brokeList, String topic, String fileOffset,
//                         Function<byte[], Boolean> taskHandler) {
//        Preconditions.checkNotNull(brokeList);
//        Preconditions.checkNotNull(topic);
//        Preconditions.checkNotNull(taskHandler);
//        this.brokeList = brokeList;
//        this.topic = topic;
//        this.fileOffset = fileOffset;
//        this.taskHandler = taskHandler;
//    }
//    public void initialize() {
//        consumerConnector = KafkaUtil.getSimpleConsumer(brokeList, topic);
//    }
//    public void consumeForever() {
//        Preconditions.checkNotNull(consumerConnector);
//        log.info("Consuming items from the kestrel queue forever.");
//        long offset = 0;
//        try{
//            offset = Integer.parseInt(FileUtils.readFileToString(new File(fileOffset)));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        while(true) {
//            try {
//                Thread.sleep(backoffDelayMs);
//            } catch (InterruptedException e) {
//                log.log(Level.INFO, "Interrupted while sleeping.", e);
//            }
//
//            FetchRequest req = new FetchRequest(topic, 0, offset, 1024 * 1000 * 1000);
//            ByteBufferMessageSet messageSet = consumerConnector.fetch(req);
//            boolean hasTask = false;
//            for (MessageAndOffset messageAndOffset : messageSet) {
//                taskHandler.apply(KafkaUtil.getMessage(messageAndOffset.message()));
//                offset = messageAndOffset.offset();
//                hasTask = true;
//            }
//            System.out.println(offset);
//            try{
//                FileUtils.writeStringToFile(new File(fileOffset), String.valueOf(offset));
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//            if (!hasTask) {
//                // Back off exponentially (capped).
//                backoffDelayMs = Math.min(backoffDelayMs * 2, MAX_BACKOFF_DELAY_MS);
//                log.info("No work, backing off for " + backoffDelayMs + " ms.");
//            } else {
//                //stats.incrementAndGet();
//                backoffDelayMs = MIN_DELAY_MS;
//            }
//
//        }
//    }
//}

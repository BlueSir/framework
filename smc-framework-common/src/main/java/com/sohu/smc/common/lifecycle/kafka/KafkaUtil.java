//package com.sohu.smc.dropwizard.lifecycle.kafka;
//
//import kafka.consumer.ConsumerConfig;
//import kafka.javaapi.consumer.ConsumerConnector;
//import kafka.javaapi.consumer.SimpleConsumer;
//import kafka.javaapi.producer.Producer;
//import kafka.message.Message;
//import kafka.producer.ProducerConfig;
//
//import java.nio.ByteBuffer;
//import java.util.Hashtable;
//import java.util.Properties;
//
///**
// * 静态变量
// * User: qinqd
// * Date: 12-3-12
// * Time: 上午10:28
// * To change this template use File | Settings | File Templates.
// */
//public class KafkaUtil {
//    public static Hashtable<String,Producer> Producer_INSTANCE= new Hashtable<String,Producer>();
//    public static Hashtable<String,ConsumerConnector> Consumer_INSTANCE= new Hashtable<String,ConsumerConnector>();
//    public static Hashtable<String,SimpleConsumer> SimpleConsumer_INSTANCE= new Hashtable<String,SimpleConsumer>();
//
//    public static Producer getProducer(String zkConnect, String topic){
//        if(Producer_INSTANCE.containsKey(topic)){
//            return Producer_INSTANCE.get(topic);
//        }
//
//        Properties props = new Properties();
//        props.put("serializer.class", "kafka.serializer.DefaultEncoder");
//        props.put("zk.connect", zkConnect);
//        //props.put("broker.list", "0:" + KafkaProperties.kafkaServerURL +  ":9092");
//        // Use random partitioner. Don't need the key type. Just set it to Integer.
//        // The message is of type String.
//        Producer producer = new Producer<Integer, String>(new ProducerConfig(props));
//        Producer_INSTANCE.put(topic, producer);
//        return producer;
//    }
//
//    public static ConsumerConnector getConsumer(String zkConnect, String topic){
//        if(Consumer_INSTANCE.containsKey(topic)){
//            return Consumer_INSTANCE.get(topic);
//        }
//
//        Properties props = new Properties();
//        props.put("zk.connect", zkConnect);
//        props.put("groupid", topic);
//        props.put("zk.sessiontimeout.ms", "400");
//        props.put("zk.synctime.ms", "200");
//        props.put("autocommit.interval.ms", "1000");
//
//        ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector( new ConsumerConfig(props));
//        Consumer_INSTANCE.put(topic, consumer);
//        return consumer;
//    }
//
//    public static SimpleConsumer getSimpleConsumer(String brokeList, String topic){
//        if(SimpleConsumer_INSTANCE.containsKey(topic)){
//            return SimpleConsumer_INSTANCE.get(topic);
//        }
//
//        SimpleConsumer simpleConsumer = new SimpleConsumer(brokeList,
//                KafkaConfig.kafkaServerPort,
//                KafkaConfig.connectionTimeOut,
//                KafkaConfig.kafkaProducerBufferSize);
//
//        SimpleConsumer_INSTANCE.put(topic,simpleConsumer);
//        return simpleConsumer;
//    }
//
//    public static byte[] getMessage(Message message) {
//        ByteBuffer buffer = message.payload();
//        byte[] bytes = new byte[buffer.remaining()];
//        buffer.get(bytes);
//        return bytes;
//    }
//
//
//}

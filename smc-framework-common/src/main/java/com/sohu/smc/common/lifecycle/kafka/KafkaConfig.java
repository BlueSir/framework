/*
 * Copyright 2010 LinkedIn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sohu.smc.common.lifecycle.kafka;

import com.sohu.smc.common.util.IpUtil;
import com.sohu.smc.common.zk.PropertyConfig;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class KafkaConfig {
    private final int kafkaProducerBufferSize = 64 * 1024;
    private final int connectionTimeOut = 100000;
    private final int reconnectInterval = 10000;

//    private final HashSet<String> topicSet = new HashSet<String>();
//    private final Hashtable<String, Integer> msgSize = new Hashtable<String, Integer>();


    public final ProducerConfig getProducer() {
        Properties props = new Properties();
        props.put("serializer.class", "kafka.serializer.DefaultEncoder");
        props.put("zk.connect", PropertyConfig.getZookeeperAddress());
        props.put("partitioner.class", "com.sohu.smc.common.lifecycle.kafka.RoundRobinPartitioner");
        return new ProducerConfig(props);
    }

    /**
     * 本机一组
     *
     * @param topic
     * @return
     */
    public final ConsumerConfig getConsumer(String topic) {
        return getConsumer(topic, IpUtil.getIp());
    }

    /**
     * 多个consumer在同一group并行消费
     *
     * @param topic
     * @param group
     * @return
     */
    public final ConsumerConfig getConsumer(String topic, String group) {
        Properties props = new Properties();
        props.put("zk.connect", PropertyConfig.getZookeeperAddress());
        props.put("groupid", group);
        props.put("zk.sessiontimeout.ms", "40000");
        props.put("zk.synctime.ms", "200");
        props.put("autocommit.interval.ms", "1000");

        return new ConsumerConfig(props);
    }

    public final SimpleConsumer getSimpleConsumer(String brokeList, String topic) {
        SimpleConsumer simpleConsumer = new SimpleConsumer(brokeList, Integer.parseInt(PropertyConfig.getKafkaPort()), connectionTimeOut, kafkaProducerBufferSize);
        return simpleConsumer;
    }

}

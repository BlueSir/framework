package com.sohu.smc.common.lifecycle.kafka;

import java.util.concurrent.atomic.AtomicInteger;

import kafka.producer.Partitioner;

/**
 * 采取轮循的方式,将消息均衡的发送到kafka集群上
 * @author shaojieyue
 * @date 2013-06-21 10:59:43
 */
public class RoundRobinPartitioner implements Partitioner {
	/**轮循均衡负载计算因子*/
	private static final AtomicInteger roundRobinDivisor = new AtomicInteger(0);
	
	@Override
	public int partition(Object key, int serverCount) {
		if(roundRobinDivisor.intValue()<0){
			roundRobinDivisor.set(0);
		}
		
		return roundRobinDivisor.incrementAndGet()%serverCount;
	}
	
}

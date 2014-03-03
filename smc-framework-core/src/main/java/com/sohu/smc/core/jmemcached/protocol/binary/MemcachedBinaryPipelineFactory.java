package com.sohu.smc.core.jmemcached.protocol.binary;

import com.sohu.smc.core.jmemcached.MemcachedHandler;
import com.sohu.smc.core.jmemcached.protocol.MemcachedCommandHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.DefaultChannelGroup;


public class MemcachedBinaryPipelineFactory implements ChannelPipelineFactory {

    private final MemcachedBinaryCommandDecoder decoder =  new MemcachedBinaryCommandDecoder();
    private final MemcachedCommandHandler memcachedCommandHandler;
    private final MemcachedBinaryResponseEncoder memcachedBinaryResponseEncoder = new MemcachedBinaryResponseEncoder();

    public MemcachedBinaryPipelineFactory(MemcachedHandler memcachedHandler, String version, boolean verbose, int idleTime, DefaultChannelGroup channelGroup) {
        memcachedCommandHandler = new MemcachedCommandHandler(memcachedHandler, version, verbose, idleTime, channelGroup);
    }

    public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(
                decoder,
                memcachedCommandHandler,
                memcachedBinaryResponseEncoder
        );
    }
}

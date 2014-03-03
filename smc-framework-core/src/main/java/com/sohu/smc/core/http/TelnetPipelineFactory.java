package com.sohu.smc.core.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;

/**
 * Created with IntelliJ IDEA.
 * User: Qinqd
 * Date: 12-7-22
 * Time: 上午12:42
 * To change this template use File | Settings | File Templates.
 */
public class TelnetPipelineFactory {
    private static final ChannelBuffer[] DELIMITERS = Delimiters.lineDelimiter();

    public static void addPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024, DELIMITERS));
    }
}

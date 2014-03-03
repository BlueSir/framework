package com.sohu.smc.core.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.nio.charset.Charset;

/**
 * Splits a ChannelBuffer in multiple space separated words.
 */
public final class WordSplitter extends OneToOneDecoder {

    private static final Charset CHARSET = Charset.forName("ISO-8859-1");

    /**
     * Constructor.
     */
    public WordSplitter() {
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx,
                            final Channel channel,
                            final Object msg) throws Exception {
        return splitString(((ChannelBuffer) msg).toString(CHARSET), ' ');
    }

    private String[] splitString(final String s, final char c) {
        final char[] chars = s.toCharArray();
        int num_substrings = 1;
        for (final char x : chars) {
            if (x == c) {
                num_substrings++;
            }
        }
        final String[] result = new String[num_substrings];
        final int len = chars.length;
        int start = 0;  // starting index in chars of the current substring.
        int pos = 0;    // current index in chars.
        int i = 0;      // number of the current substring.
        for (; pos < len; pos++) {
            if (chars[pos] == c) {
                result[i++] = new String(chars, start, pos - start);
                start = pos + 1;
            }
        }
        result[i] = new String(chars, start, pos - start);
        return result;
    }

}

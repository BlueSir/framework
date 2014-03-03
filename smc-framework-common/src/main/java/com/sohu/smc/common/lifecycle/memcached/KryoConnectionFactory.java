package com.sohu.smc.common.lifecycle.memcached;

import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.transcoders.Transcoder;

public class KryoConnectionFactory extends DefaultConnectionFactory {

    /*
    * (non-Javadoc)
    *
    * @see net.spy.memcached.ConnectionFactory#getDefaultTranscoder()
    */
    public Transcoder<Object> getDefaultTranscoder() {
        return new KryoSerializingTranscoder();
    }
}

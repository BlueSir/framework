package com.sohu.smc.common.lifecycle.memcached;

import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.OperationFactory;
import net.spy.memcached.protocol.binary.BinaryMemcachedNodeImpl;
import net.spy.memcached.protocol.binary.BinaryOperationFactory;
import net.spy.memcached.transcoders.Transcoder;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Default connection factory for binary wire protocol connections.
 */
public class KryoBinaryConnectionFactory extends DefaultConnectionFactory {

    /**
     * Create a DefaultConnectionFactory with the default parameters.
     */
    public KryoBinaryConnectionFactory() {
        super();
    }

    /**
     * Create a BinaryConnectionFactory with the given maximum operation queue
     * length, and the given read buffer size.
     */
    public KryoBinaryConnectionFactory(int len, int bufSize) {
        super(len, bufSize);
    }

    /**
     * Construct a BinaryConnectionFactory with the given parameters.
     *
     * @param len the queue length.
     * @param bufSize the buffer size
     * @param hash the algorithm to use for hashing
     */
    public KryoBinaryConnectionFactory(int len, int bufSize, HashAlgorithm hash) {
        super(len, bufSize, hash);
    }

    @Override
    public MemcachedNode createMemcachedNode(SocketAddress sa, SocketChannel c,
                                             int bufSize) {
        boolean doAuth = false;
        return new BinaryMemcachedNodeImpl(sa, c, bufSize,
                createReadOperationQueue(), createWriteOperationQueue(),
                createOperationQueue(), getOpQueueMaxBlockTime(), doAuth,
                getOperationTimeout());
    }

    @Override
    public OperationFactory getOperationFactory() {
        return new BinaryOperationFactory();
    }

    @Override
    protected String getName() {
        return "BinaryConnectionFactory";
    }

    /*
    * (non-Javadoc)
    *
    * @see net.spy.memcached.ConnectionFactory#getDefaultTranscoder()
    */
    public Transcoder<Object> getDefaultTranscoder() {
        return new KryoSerializingTranscoder();
    }
}

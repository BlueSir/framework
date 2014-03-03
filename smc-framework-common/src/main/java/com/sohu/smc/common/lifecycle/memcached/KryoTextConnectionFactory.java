//package com.sohu.smc.common.lifecycle.memcached;
//
//import net.spy.memcached.DefaultConnectionFactory;
//import net.spy.memcached.HashAlgorithm;
//import net.spy.memcached.MemcachedNode;
//import net.spy.memcached.OperationFactory;
//import net.spy.memcached.protocol.ascii.AsciiMemcachedNodeImpl;
//import net.spy.memcached.protocol.ascii.AsciiOperationFactory;
//
//import java.net.SocketAddress;
//import java.nio.channels.SocketChannel;
//
///**
// * Default connection factory for binary wire protocol connections.
// */
//public class KryoTextConnectionFactory extends DefaultConnectionFactory {
//
//    /**
//     * Create a DefaultConnectionFactory with the default parameters.
//     */
//    public KryoTextConnectionFactory() {
//        super();
//    }
//
//    /**
//     * Create a BinaryConnectionFactory with the given maximum operation queue
//     * length, and the given read buffer size.
//     */
//    public KryoTextConnectionFactory(int len, int bufSize) {
//        super(len, bufSize);
//    }
//
//    /**
//     * Construct a BinaryConnectionFactory with the given parameters.
//     *
//     * @param len     the queue length.
//     * @param bufSize the buffer size
//     * @param hash    the algorithm to use for hashing
//     */
//    public KryoTextConnectionFactory(int len, int bufSize, HashAlgorithm hash) {
//        super(len, bufSize, hash);
//    }
//
//    @Override
//    public MemcachedNode createMemcachedNode(SocketAddress sa, SocketChannel c, int bufSize) {
//        return new AsciiMemcachedNodeImpl(sa, c, bufSize,
//                createReadOperationQueue(), createWriteOperationQueue(),
//                createOperationQueue(), getOpQueueMaxBlockTime(),
//                getOperationTimeout());
//    }
//
//    @Override
//    public OperationFactory getOperationFactory() {
//        return new AsciiOperationFactory();
//    }
//
//    @Override
//    protected String getName() {
//        return "AsciiOperationFactory";
//    }
//
//    /*
//    * @see net.spy.memcached.ConnectionFactory#getDefaultTranscoder()
//    */
////    public Transcoder<Object> getDefaultTranscoder() {
////        return new KryoSerializingTranscoder();
////    }
//
//    /**
//     * 超时时间
//     *
//     * @return
//     */
//    public long getOperationTimeout() {
//        return 100;
//    }
//}

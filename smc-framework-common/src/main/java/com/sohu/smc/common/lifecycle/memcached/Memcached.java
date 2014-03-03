package com.sohu.smc.common.lifecycle.memcached;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.spy.memcached.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for creating a memcached client.
 *
 * @author William Farner
 */
public class Memcached {
    private static Logger log = Logger.getLogger(Memcached.class.getName());

    private static final Joiner serverListJoiner = Joiner.on(" ");

    /**
     * Creates a new memcached client for use as an interface to a kestrel queue.
     *
     * @param servers The kestrel servers to use.
     * @return A memcached client.
     */
    public static MemcachedClient newKestrelClient(List<String> servers) {
        try {
            return new MemcachedClient(new KestrelConnectionFactory(),
                    AddrUtil.getAddresses(serverListJoiner.join(servers)));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to build server list.", e);
            return null;
        }
    }

    /**
     * Creates a new generic memcached client that uses daemon threads for IO and the binary protocol.
     *
     * @param servers The memcached servers to use.
     * @return A memcached client.
     */
    public static MemcachedClient newMemcachedClient(List<String> servers) {
        try {
            return new MemcachedClient(new DaemonThreadBinaryConnectionFactory(),
                    AddrUtil.getAddresses(serverListJoiner.join(servers)));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to build server list.", e);
            return null;
        }
    }

    /**
     * Connection factory to use for interfacing with kestrel.
     */
    private static class KestrelConnectionFactory extends DaemonThreadAsciiConnectionFactory {
        @Override
        public NodeLocator createLocator(List<MemcachedNode> nodes) {
            return new KestrelNodeLocator(nodes);
        }

        //todo use it 2.8.1
//        @Override
//        public boolean shouldOptimize() {
//            return false;
//        }

    }

    /**
     * Connection factory that uses daemon threads, so the process completes after the application
     * thread(s) complete. This connection factory uses the default (ASCII) protocol.
     */
    private static class DaemonThreadAsciiConnectionFactory extends DefaultConnectionFactory {
        @Override
        public boolean isDaemon() {
            return true;
        }
    }

    /**
     * Connection factory that uses daemon threads, so the process completes after the application
     * thread(s) complete. This connection factory uses the binary protocol.
     */
    private static class DaemonThreadBinaryConnectionFactory extends BinaryConnectionFactory {
        @Override
        public boolean isDaemon() {
            return true;
        }
    }

    /**
     * Node locator for kestrel. This issues requests to a random node.
     */
    private static class KestrelNodeLocator implements NodeLocator {
        private final List<MemcachedNode> nodes = Lists.newArrayList();
        private final Random rand = new Random();

        public KestrelNodeLocator(List<MemcachedNode> nodes) {
            Preconditions.checkNotNull(nodes);
            Preconditions.checkState(nodes.size() > 0);
            updateNodes(nodes);
        }

        @Override
        public MemcachedNode getPrimary(String s) {
            return nodes.get(rand.nextInt(nodes.size()));
        }

        @Override
        public Iterator<MemcachedNode> getSequence(String s) {
            List<MemcachedNode> shuffled = Lists.newLinkedList(nodes);
            Collections.shuffle(shuffled);
            return shuffled.iterator();
        }

        @Override
        public Collection<MemcachedNode> getAll() {
            return nodes;
        }

        @Override
        public NodeLocator getReadonlyCopy() {
            return new KestrelNodeLocator(nodes);
        }

        public void updateLocator(List<MemcachedNode> memcachedNodes) {
//            updateNodes(memcachedNodes);
        }

        private void updateNodes(Collection<MemcachedNode> nodes) {
            this.nodes.clear();
            this.nodes.addAll(nodes);
        }
    }
}

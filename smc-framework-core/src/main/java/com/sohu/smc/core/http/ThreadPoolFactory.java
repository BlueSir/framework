/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package com.sohu.smc.core.http;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: ThreadPoolFactory</p>
 * <p>Description: Stats instrumented thread pool executor factory</p>
 */
public class ThreadPoolFactory extends ThreadPoolExecutor implements ThreadFactory {
    /**  */
    private static final long serialVersionUID = 5127908418248445054L;
    /**
     * Serial number factory for thread names
     */
    protected final AtomicInteger serial = new AtomicInteger(0);
    /**
     * The pool name
     */
    protected final String name;
    /**
     * The threading level json object
     */
    protected static final JSONObject threadingMetrics = new JSONObject();
    /**
     * This pool's threading metrics json object
     */
    protected final Map<String, Number> poolMetrics = new HashMap<String, Number>();
    /**
     * The supplied metric names
     */
    protected final Set<String> metricNames = new HashSet<String>();
    /**
     * The metric points
     */
    protected final String[] points = new String[]{"activeThreads", "poolSize", "largestPoolSize", "completedTasks"};

    /**
     * Creates a new ThreadPool
     *
     * @param name The name property for the MBean ObjectName
     * @return a new thread pool
     */
    public static ThreadPoolFactory newCachedThreadPool(String name) {
        return new ThreadPoolFactory(name);
    }

    /**
     * Creates a new ThreadPool
     *
     * @param name The name property for the MBean ObjectName
     */
    private ThreadPoolFactory(String name) {
        super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        setThreadFactory(this);
        this.name = name;
        try {
            threadingMetrics.put(name, poolMetrics);
            String prefix = "threadPools.[" + name + "].";
            for (String s : points) {
                metricNames.add(prefix + s);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to register management interface for pool [" + name + "]", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getProvidedMetricNames() {
        return metricNames;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.concurrent.ThreadFactory#newThread(Runnable)
     */
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, name + "Thread#" + serial.incrementAndGet());
        t.setDaemon(true);
        return t;
    }
}
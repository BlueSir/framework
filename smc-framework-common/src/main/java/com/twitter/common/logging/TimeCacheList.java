package com.twitter.common.logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Expires keys that have not been updated in the configured number of seconds.
 * The algorithm used will take between expirationSecs and
 * expirationSecs * (1 + 1 / (numBuckets-1)) to actually expire the message.
 * <p/>
 * get, put, remove, containsKey, and size take O(numBuckets) time to run.
 * <p/>
 * The advantage of this design is that the expiration thread only locks the object
 * for O(1) time, meaning the object is essentially always available for gets/puts.
 */
public class TimeCacheList<T> {
    //this default ensures things expire at most 50% past the expiration time
    private final static int DEFAULT_NUM_BUCKETS = 3;
    private final static int DEFAULT_CAPACITY = 1000;
    private final LinkedList<List<T>> buckets;
    private List<T> freecontainer = new ArrayList<T>(DEFAULT_CAPACITY);
    private final Object lock = new Object();
    private final Object timer_lock = new Object();
    private final Thread cleaner;
    private final CacheCallback callback;
    private final int numBuckets;

    public TimeCacheList(int expirationSecs, CacheCallback<T> callback) {
        this(DEFAULT_NUM_BUCKETS, expirationSecs, callback);
    }

    public TimeCacheList(int numBuckets, int expirationSecs, CacheCallback<T> callback) {
        if (numBuckets < 2) {
            throw new IllegalArgumentException("numBuckets must be >= 2");
        }
        this.numBuckets = numBuckets;
        buckets = new LinkedList<List<T>>();
        for (int i = 0; i < numBuckets; i++) {
            buckets.add(new ArrayList<T>(DEFAULT_CAPACITY));
        }

        this.callback = callback;
        final long sleepTime = expirationSecs * 1000L;
        cleaner = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(sleepTime);

                        synchronized (timer_lock) {
                            List<T> dead = buckets.removeLast();
                            buckets.addFirst(freecontainer);
                            freecontainer = dead;
                            TimeCacheList.this.callback.flushCache(dead);

//                            System.out.println("clear size:" + freecontainer.size());
                            if (TimeCacheList.this.callback != null) {
                                Iterator<T> it = dead.iterator();
                                while (it.hasNext()) {
                                    T obj = it.next();
                                    it.remove();
                                    TimeCacheList.this.callback.returnObject(obj);
                                }
                            }
                            dead = null;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public void put(T t) {
        synchronized (lock) {
            int i = (int) (Math.random() * 10);
            int index = i % numBuckets;
            buckets.get(index).add(t);
        }
    }

    public int size() {
        synchronized (lock) {
            int size = 0;
            for (List<T> bucket : buckets) {
                size += bucket.size();
            }
            return size;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            cleaner.interrupt();
        } finally {
            super.finalize();
        }
    }


}
// =================================================================================================
// Copyright 2011 Twitter, Inc.
// -------------------------------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this work except in compliance with the License.
// You may obtain a copy of the License in the LICENSE file, or at:
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// =================================================================================================

package com.twitter.common.logging;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.sohu.smc.common.util.IpUtil;
import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;
import com.twitter.common.thrift.Thrift;
import com.twitter.common.thrift.ThriftFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.apache.scribe.LogEntry;
import org.apache.scribe.ResultCode;
import org.apache.scribe.scribe;
import org.apache.thrift.TException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static org.apache.scribe.ResultCode.OK;
import static org.apache.scribe.ResultCode.TRY_LATER;

/**
 * Implementation of the scribe client, logs message directly to scribe.
 *
 * @author William Farner
 */
public class ScribeLog implements Log<LogEntry, ResultCode> {
//    private static final Logger LOG = Logger.getLogger(ScribeLog.class.getName());

    // Connection pool options.
    private static final int MAX_CONNECTIONS_PER_HOST = 5;
    private static final Amount<Long, Time> REQUEST_TIMEOUT = Amount.of(4L, Time.SECONDS);

    // Max retries per request before giving up.
    private static final int MAX_RETRIES = 3;
    private static final String outf = IpUtil.getIp() + " %s %s\n";
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final scribe.Iface client;
    private ObjectPool<LogEntry> pool;
//    private ExecutorService flushtask = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final TimeCacheList<LogEntry> cache = new TimeCacheList<LogEntry>(3, new CacheCallback<LogEntry>() {
        @Override
        public void returnObject(LogEntry log) {
            try {
                pool.returnObject(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void flushCache(List<LogEntry> list) {
            if (list.size() == 0) {
//                System.out.println("list is empty");
                return;
            }

//            System.out.println("send log:====================" + list.size());
            final List<LogEntry> tmp = new ArrayList<LogEntry>(600);

//            int a = list.size() / num_unit, b = list.size() % num_unit;
            for (int i = 0; i < list.size(); i++) {
                tmp.add(list.get(i));
                if (i % 600 == 0 || i == list.size() - 1) {
                    try {
                        client.Log(tmp);
                    } catch (TException e) {
                        e.printStackTrace();
                    } finally {
                        tmp.clear();
                    }
                }
            }

        }
    });

    public ScribeLog(List<InetSocketAddress> hosts) {
        this(hosts, MAX_CONNECTIONS_PER_HOST);
    }

    /**
     * Creats a new scribe client, connecting to the given hosts on the given port.
     *
     * @param hosts Thrift servers to connect to.
     * @throws ThriftFactory.ThriftFactoryException
     *          If the client could not be created.
     */
    public ScribeLog(List<InetSocketAddress> hosts, int connect_count) {
        Preconditions.checkNotNull(hosts);

        Thrift<scribe.Iface> thrift = ThriftFactory.create(scribe.Iface.class)
                .withMaxConnectionsPerEndpoint(connect_count)
                .useFramedTransport(true)
                .build(Sets.newHashSet(hosts));

        client = thrift.builder()
                .withRetries(MAX_RETRIES)
                .withRequestTimeout(REQUEST_TIMEOUT)
                .create();

        this.pool = new StackObjectPool(new LogEntryFactory(), connect_count * 10, connect_count);
    }


    @Override
    public ResultCode log(LogEntry entry) {
        return log(Arrays.asList(entry));
    }

    @Override
    public ResultCode log(List<LogEntry> entries) {
        try {
            return client.Log(entries);
        } catch (TException e) {
            System.err.println("Failed to submit log request!.");
            return TRY_LATER;
        }
    }

    public ResultCode append(String scribe_category, String message) {
        message = format(outf, formatter.format(new Date()), message);

        try {
            LogEntry entry = pool.borrowObject();
            entry.setCategory(scribe_category).setMessage(message);
            cache.put(entry);

            return OK;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return TRY_LATER;
    }

    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    public ResultCode err(String scribe_category, Throwable t) {
        return err(scribe_category, t, "");
    }

    public ResultCode err(final String scribe_category, Throwable t, final String msg) {

        try {
            String message = msg + getStackTrace(t);
            return append(scribe_category, msg + "  \n" + message);
        } catch (Exception e) {
            e.printStackTrace();
            return TRY_LATER;
        }
    }

    @Override
    public void flush() {
        // No-op.
    }

    private class LogEntryFactory extends BasePoolableObjectFactory<LogEntry> {

        @Override
        public LogEntry makeObject() throws Exception {
            return new LogEntry();
        }

        public void passivateObject(LogEntry entry) {
            entry.clear();
//            System.out.println(entry.getMessage() + "====>>>");
        }

        public void destroyObject(LogEntry entry) {
            entry.clear();
            entry = null;
        }
    }
}



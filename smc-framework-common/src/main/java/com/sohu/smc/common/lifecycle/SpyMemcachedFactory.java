package com.sohu.smc.common.lifecycle;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.shared.SharedCount;
import com.netflix.curator.framework.recipes.shared.SharedCountListener;
import com.netflix.curator.framework.recipes.shared.SharedCountReader;
import com.netflix.curator.framework.recipes.shared.SharedValue;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.retry.RetryOneTime;
import com.sohu.smc.common.zk.PropertyConfig;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Spymemcached Factory User: Qinqd Date: 12-7-5 Time: 下午1:58 To change this
 * template use File | Settings | File Templates.
 */
public class SpyMemcachedFactory {
    private final SpyMemcachedBean bean;
    private MemcachedClient clientMaster = null;
    private MemcachedClient clientSlaver = null;
    private static Resource resource = null;
    private static BeanFactory ctx = null;
    int Limit = 50; // 50ms

    static {
        init();
    }

    public static synchronized void init() {
        resource = new ClassPathResource("memcacheContext.xml");
        if (resource == null) {
            System.err.println("[cacheContext.xml] is not exist.");
            System.exit(0);
        }
        ctx = new XmlBeanFactory(resource);
    }


    private final String module;

    public SpyMemcachedFactory(String module) {
        bean = (SpyMemcachedBean) ctx.getBean(module);
        try {
            clientMaster = new MemcachedClient(AddrUtil.getAddresses(bean.getMasterAddress().replaceAll(",", " ")));
//            clientMaster = new MemcachedClient(new KryoTextConnectionFactory(), AddrUtil.getAddresses(bean.getMasterAddress().replaceAll(",", " ")));
            if (bean.isFailover() && bean.getSlaveAddress() != null) {
//                clientSlaver = new MemcachedClient(new KryoTextConnectionFactory(), AddrUtil.getAddresses(bean.getSlaveAddress().replaceAll(",", " ")));
                clientSlaver = new MemcachedClient(AddrUtil.getAddresses(bean.getSlaveAddress().replaceAll(",", " ")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.module = module;
        if (bean.isFailover()) {
            try {
                CuratorFramework zkClient = CuratorFrameworkFactory.newClient(PropertyConfig.getZookeeperAddress(), new RetryOneTime(3));
                zkClient.start();


                SharedCount count = new SharedCount(zkClient, "/memcached/" + module, 1);
                final SharedValue value = new SharedValue(zkClient,
                        "/memcached/address_" + module, new JSONObject()
                        .put("master", bean.getMasterAddress())
                        .put("slaver", bean.getSlaveAddress())
                        .toString().getBytes());

                count.addListener(new SharedCountListener() {
                    @Override
                    public void countHasChanged(SharedCountReader sharedCount, int newCount) throws Exception {
                        System.out.println(newCount);
                        JSONObject jsonObject = new JSONObject(new String(value.getValue()));
                        try {
                            //todo open it in spy2.8.1
                            clientMaster = new MemcachedClient(AddrUtil.getAddresses(jsonObject.getString("master").replaceAll(",", " ")));
//                            clientMaster = new MemcachedClient(new KryoTextConnectionFactory(), AddrUtil.getAddresses(jsonObject.getString("master").replaceAll(",", " ")));
                            if (bean.isFailover()) {
                                if (bean.getSlaveAddress() != null && !"".equals(jsonObject.getString("slaver"))) {
                                    clientSlaver = new MemcachedClient(AddrUtil.getAddresses(jsonObject.getString("slaver").replaceAll(",", " ")));
//                                    clientSlaver = new MemcachedClient(new KryoTextConnectionFactory(), AddrUtil.getAddresses(jsonObject.getString("slaver").replaceAll(",", " ")));
                                } else {
                                    clientSlaver = null;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void set(String key, int timeout, Object v) {
        set(clientMaster, key, timeout, v);
        if (bean.isFailover() && clientSlaver != null) {
            set(clientSlaver, key, timeout, v);
        }
    }

    public void touch(String key, int timeout) {
        touch(clientMaster, key, timeout);
        if (bean.isFailover() && clientSlaver != null) {
            touch(clientSlaver, key, timeout);
        }
    }

    public void add(String key, int timeout, Object v) {
        add(clientMaster, key, timeout, v);
        if (bean.isFailover() && clientSlaver != null) {
            add(clientSlaver, key, timeout, v);
        }
    }

    private void add(MemcachedClient client, String key, int timeout, Object v) {
        long tick = System.currentTimeMillis();
        if (timeout == -1)
            timeout = 0;

        client.add(key, timeout, v);

        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "add");
        }
    }

    private void set(MemcachedClient client, String key, int timeout, Object v) {
        long tick = System.currentTimeMillis();
        if (timeout == -1)
            timeout = 0;

        client.set(key, timeout, v);

        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "set");
        }
    }

    private void touch(MemcachedClient client, String key, int timeout) {
        long tick = System.currentTimeMillis();
        if (timeout == -1)
            timeout = 0;

        //todo use it 2.8.1
        client.touch(key, timeout);

        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "set");
        }
    }

    public long incr(String key) {
        long ret = incr(clientMaster, key);
        if (bean.isFailover() && clientSlaver != null) {
            incr(clientSlaver, key);
        }

        return ret;
    }

    public long incr(String key, long v) {
        long ret = incr(clientMaster, key, v);
        if (bean.isFailover() && clientSlaver != null) {
            incr(clientSlaver, key, v);
        }
        return ret;
    }

    private long incr(MemcachedClient client, String key) {
        long tick = System.currentTimeMillis();
        long ret = incr(client, key, 1L);
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "incr");
        }
        return ret;
    }


    private long incr(MemcachedClient client, String key, long v) {

        long tick = System.currentTimeMillis();
        long ret = client.incr(key, Integer.parseInt(v + ""));
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "incr");
        }
        return ret;
    }

    public void incrNew(String key, int timeout, int v) {
        incrNew(clientMaster, key, timeout, v);
        if (bean.isFailover() && clientSlaver != null) {
            incrNew(clientSlaver, key, timeout, v);
        }
    }

    private void incrNew(MemcachedClient client, String key, int timeout, int v) {
        if (timeout == -1)
            timeout = 0;
        long tick = System.currentTimeMillis();
        if (client.incr(key, v) == -1) {
            client.set(key, timeout, String.valueOf(v));
        }
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "incr");
        }
    }

    public <T> T get(String key) {
        if (bean.isFailover() && clientSlaver != null) {
            return (T) get(clientSlaver, key);
        } else {
            return (T) get(clientMaster, key);
        }
    }

    private <T> T get(MemcachedClient client, String key) {
        long tick = System.currentTimeMillis();
        // Try to get a value, for up to 5 seconds, and cancel if it doesn't
        // return
        T myObj = null;
        Future<Object> f = client.asyncGet(key);
        try {
            myObj = (T) f.get(50, MILLISECONDS);
        } catch (Exception e) {
            // Since we don't need this, go ahead and cancel the operation. This
            // is not strictly necessary, but it'll save some work on the
            // server.
            e.printStackTrace();
            f.cancel(false);
            // Do other timeout related stuff
        }
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "get");
        }
        return myObj;
    }


    public Map<String, Object> getBulk(String key) {
        if (bean.isFailover() && clientSlaver != null) {
            return getBulk(clientSlaver, key);
        } else {
            return getBulk(clientMaster, key);
        }
    }

    private Map<String, Object> getBulk(MemcachedClient client, String key) {
        long tick = System.currentTimeMillis();
        // Try to get a value, for up to 5 seconds, and cancel if it doesn't
        // return
        Map<String, Object> myObj = null;
        Future<Map<String, Object>> f = client.asyncGetBulk(key.split(" "));

        try {
            myObj = f.get(50, MILLISECONDS);
        } catch (Exception e) {
            // Since we don't need this, go ahead and cancel the operation. This
            // is not strictly necessary, but it'll save some work on the
            // server.
            f.cancel(false);
            // Do other timeout related stuff
        }
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "multiget");
        }
        return myObj;
    }

    public String getString(String key) {
        if (bean.isFailover() && clientSlaver != null) {
            return getString(clientMaster, key);
        } else {
            return getString(clientMaster, key);
        }
    }

    private String getString(MemcachedClient client, String key) {
        long tick = System.currentTimeMillis();
        String obj = get(client, key);
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "get");
        }
        if (obj == null)
            return null;
        return (String) obj;
    }

    public long getLong(String key) {
        if (bean.isFailover() && clientSlaver != null) {
            return getLong(clientMaster, key);
        } else {
            return getLong(clientMaster, key);
        }
    }

    private long getLong(MemcachedClient client, String key) {
        long tick = System.currentTimeMillis();
        Object obj = get(client, key);
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "get");
        }
        if (obj == null)
            return -1;
        return (Long) obj;
    }

    public long getInt(String key) {
        if (bean.isFailover() && clientSlaver != null) {
            return getInt(clientMaster, key);
        } else {
            return getInt(clientMaster, key);
        }
    }

    private long getInt(MemcachedClient client, String key) {
        long tick = System.currentTimeMillis();
        Object obj = get(client, key);
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "get");
        }
        if (obj == null)
            return -1;
        return Integer.parseInt((String) obj);
    }

    public void delete(String key) {
        remove(clientMaster, key);
        if (bean.isFailover() && clientSlaver != null) {
            remove(clientSlaver, key);
        }
    }

    private void remove(MemcachedClient client, String key) {
        long tick = System.currentTimeMillis();
        client.delete(key);
        tick = System.currentTimeMillis() - tick;
        if (tick > Limit) {
            log(key, tick, "delete");
        }
    }

    private void log(String key, long tick, String act) {
        /*
           * try{ LogFormat.log("memcached",act, module,key + "\tms:" + tick +
           * "\tnode:" +
           * client.getNodeLocator().getPrimary(key).getSocketAddress().toString()
           * + "\tlocal:" + Define.HOSTS); }catch (Exception e){
           * e.printStackTrace(); }
           */
    }

    public MemcachedClient getMasterClient() {
        return clientMaster;
    }

    public MemcachedClient getSlaverClient() {
        return clientSlaver;
    }
}

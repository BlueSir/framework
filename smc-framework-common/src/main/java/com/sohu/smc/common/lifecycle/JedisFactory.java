package com.sohu.smc.common.lifecycle;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.recipes.shared.SharedCount;
import com.netflix.curator.framework.recipes.shared.SharedCountListener;
import com.netflix.curator.framework.recipes.shared.SharedCountReader;
import com.netflix.curator.framework.state.ConnectionState;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import redis.clients.jedis.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA. User: Qinqd Date: 12-7-5 Time: 下午1:58 To change
 * this template use File | Settings | File Templates.
 */
public class JedisFactory {
    static Resource resource = null;
    static BeanFactory ctx = null;
    int Limit = 50; // 50ms

    static {
        init();
    }

    public static synchronized void init() {
        resource = new ClassPathResource("redis-beans.xml");
        ctx = new XmlBeanFactory(resource);
    }

    JedisBean bean = null;
    ShardedJedisPool clientMaster = null;
    ShardedJedisPool clientSlaver = null;

    String module = "";

    /**
     * 初始化切片池
     */
    private ShardedJedisPool initialShardedPool(String conn) {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(20);
        config.setMaxIdle(5);
        config.setMaxWait(1000l);
        config.setTestOnBorrow(false);

        /*conf.setTestWhileIdle(true);
        conf.setMinEvictableIdleTimeMillis(60000);
        conf.setTimeBetweenEvictionRunsMillis(30000);
        conf.setNumTestsPerEvictionRun(-1);*/
        // slave链接
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        String[] __conns = conn.split(",");
        for (String __conn : __conns) {
            shards.add(new JedisShardInfo(__conn.split(":")[0], Integer
                    .parseInt(__conn.split(":")[1]), "master"));
        }
        // 构造池
        return new ShardedJedisPool(config, shards);
    }

    public JedisFactory(String module) {
        bean = (JedisBean) ctx.getBean(module);
        try {
            clientMaster = initialShardedPool(bean.getMasterAddress());
            if (bean.isFailover() && bean.getSlaveAddress() != null)
                clientSlaver = initialShardedPool(bean.getSlaveAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.module = module;
        if (bean.isFailover()) {
            try {
                CuratorFramework zkClient = Utils.zkClient;

                SharedCount count = new SharedCount(zkClient, "/redis/"
                        + module, 1);
                count.start();

                JSONObject zkvalue = new JSONObject();
                zkvalue.put("master", bean.getMasterAddress())
                        .put("slaver", bean.getSlaveAddress());
                final ShareString value = new ShareString(zkClient, "/redis/address_" + module, zkvalue);
                value.start();

                count.addListener(new SharedCountListener() {
                    public void countHasChanged(SharedCountReader sharedCount,
                                                int newCount) throws Exception {
                        System.out.println(newCount);
                        JSONObject jsonObject = value.getJSON();
                        try {
                            clientMaster = initialShardedPool(jsonObject.optString("master"));
                            if (bean.isFailover()) {
                                if (bean.getSlaveAddress() != null && !"".equals(jsonObject.optString("slaver")))
                                    clientSlaver = initialShardedPool(jsonObject.optString("slaver"));
                                else {
                                    clientSlaver = null;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String get(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
        return null;
    }

    public String set(String key, String value) {
        String result = null;
        result = set(key, value, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = set(key, value, clientSlaver);
        return result;
    }

    public long zadd(String key, double score, String member) {
        long result = -1;
        result = zadd(key, score, member, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = zadd(key, score, member, clientSlaver);
        return result;

    }

    public int zadd(String key, Map<Double, String> members) {
        int result = -1;
        result = zadd(key, members, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = zadd(key, members, clientSlaver);
        return result;
    }

    public long zrem(String key, String member) {
        long result = -1;
        result = zrem(key, member, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = zrem(key, member, clientSlaver);
        return result;

    }

    public long zrem(String key, List<String> members) {
        long result = -1;
        result = zrem(key, members, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = zrem(key, members, clientSlaver);
        return result;
    }

    public long zcard(String key) {
        return zcard(key, clientMaster);
    }

    public long zcount(String key, double min, double max) {
        return zcount(key, min, max, clientMaster);
    }

    public double zscore(String key, String member) {
        return zscore(key, member, clientMaster);
    }

    public Set<String> zrange(String key, int start, int end) {
        return zrange(key, start, end, clientMaster);
    }

    public Set<Tuple> zrangeWithScores(String key, int start, int end) {
        return zrangeWithScores(key, start, end, clientMaster);
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        return zrangeByScore(key, min, max, clientMaster);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return zrangeByScoreWithScores(key, min, max, clientMaster);
    }

    public Set<String> zrangeByScore(String key, double min, double max,
                                     int offset, int count) {
        return zrangeByScore(key, min, max, offset, count, clientMaster);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                              double max, int offset, int count) {
        return zrangeByScoreWithScores(key, min, max, offset, count,
                clientMaster);
    }

    public Set<String> zrevrange(String key, int start, int end) {
        return zrevrange(key, start, end, clientMaster);
    }

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        return zrevrangeWithScores(key, start, end, clientMaster);
    }

    public Set<String> zrevrangeByScore(String key, double min, double max) {
        return zrevrangeByScore(key, min, max, clientMaster);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double min,
                                                 double max) {
        return zrevrangeByScoreWithScores(key, min, max, clientMaster);
    }

    public Set<String> zrevrangeByScore(String key, double min, double max,
                                        int offset, int count) {
        return zrevrangeByScore(key, min, max, offset, count, clientMaster);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double min,
                                                 double max, int offset, int count) {
        return zrevrangeByScoreWithScores(key, min, max, offset, count,
                clientMaster);
    }

    public long zrank(String key, String member) {
        return zrank(key, member, clientMaster);
    }

    public long zrevrank(String key, String member) {
        return zrevrank(key, member, clientMaster);
    }

    public long zremrangeByRank(String key, int start, int end) {
        long result = -1;
        result = zremrangeByRank(key, start, end, clientMaster);
        if (bean.isFailover() && clientSlaver != null) {
            result = zremrangeByRank(key, start, end, clientSlaver);
        }
        return result;
    }

    public long zremrangeByScore(String key, double min, double max) {
        long result = -1;
        result = zremrangeByScore(key, min, max, clientMaster);
        if (bean.isFailover() && clientSlaver != null) {
            result = zremrangeByScore(key, min, max, clientSlaver);
        }
        return result;
    }

    public long rpush(String key, String value) {
        long result = rpush(key, value, clientMaster);
        if (bean.isFailover() && clientSlaver != null) {
            result = rpush(key, value, clientSlaver);
        }
        return result;
    }

    public long rpush(String key, List<String> values) {
        long result = rpush(key, values, clientMaster);
        if (bean.isFailover() && clientSlaver != null) {
            result = rpush(key, values, clientSlaver);
        }
        return result;
    }

    public long llen(String key) {
        return llen(key, clientMaster);
    }

    public List<String> lrange(String key, int start, int end) {
        return lrange(key, start, end, clientMaster);
    }

    public long expire(String key, int seconds) {
        long result = this.expire(key, seconds, clientMaster);
        if (bean.isFailover() && clientSlaver != null) {
            result = this.expire(key, seconds, clientSlaver);
        }
        return result;
    }

    public long del(String key) {
        long result = this.del(key, clientMaster);
        if (bean.isFailover() && clientSlaver != null) {
            result = this.del(key, clientSlaver);
        }
        return result;
    }

    public long del(List<String> keys) {
        long result = this.del(keys, clientMaster);
        if (bean.isFailover() && clientSlaver != null) {
            result = this.del(keys, clientSlaver);
        }
        return result;
    }

    public String set(String key, String value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {

            return null;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zadd(String key, double score, String member,
                     ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zadd(key, score, member);
        } catch (Exception e) {

            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public int zadd(String key, Map<Double, String> members,
                    ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<Double> memIte = members.keySet().iterator();
            while (memIte.hasNext()) {
                double score = memIte.next();
                String member = members.get(score);
                count += jedis.zadd(key, score, member);
            }
            return count;
        } catch (Exception e) {

            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zrem(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrem(key, member);
        } catch (Exception e) {

            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zrem(String key, List<String> members, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<String> memIte = members.iterator();
            while (memIte.hasNext()) {
                String member = memIte.next();
                count += jedis.zrem(key, member);
            }
            return count;
        } catch (Exception e) {

            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zcard(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zcard(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zcount(String key, double min, double max, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zcount(key, min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public double zscore(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zscore(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrange(String key, int start, int end,
                              ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrange(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrangeWithScores(String key, int start, int end,
                                       ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeWithScores(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrangeByScore(String key, double min, double max,
                                     ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeByScore(key, min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                              double max, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrangeByScore(String key, double min, double max,
                                     int offset, int count, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeByScore(key, min, max, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                              double max, int offset, int count, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrevrange(String key, int start, int end,
                                 ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrange(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end,
                                          ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeWithScores(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrevrangeByScore(String key, double min, double max,
                                        ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeByScore(key, max, min);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double min,
                                                 double max, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrevrangeByScore(String key, double min, double max,
                                        int offset, int count, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double min,
                                                 double max, int offset, int count, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeByScoreWithScores(key, max, min, offset,
                    count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zrank(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrank(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zrevrank(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrank(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zremrangeByRank(String key, int start, int end,
                                ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zremrangeByScore(String key, double min, double max,
                                 ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zremrangeByScore(key, min, max);
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long rpush(String key, String value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.rpush(key, value);
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long rpush(String key, List<String> values, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            long length = 0;
            for (String value : values) {
                length = jedis.rpush(key, value);
            }
            return length;
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long llen(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.llen(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public List<String> lrange(String key, int start, int end,
                               ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public String rpop(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.rpop(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public String rpop(String key) {
        String result = rpop(key, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = rpop(key, clientSlaver);
        return result;
    }

    public void ltrim(String key,  ShardedJedisPool pool,int start,int end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.ltrim(key,start,end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long lpush(String key, String value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.lpush(key, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long lpush(String key, String value) {
        long result = lpush(key, value, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = lpush(key, value, clientSlaver);
        return result;
    }

    public void ltrim(String key,int start, int end) {
        ltrim(key, clientMaster,start,end );
        if (bean.isFailover() && clientSlaver != null)
            ltrim(key, clientSlaver,start, end);
    }

    public long lpush(String key, List<String> values, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            long length = 0;
            for (String value : values) {
                length = jedis.lpush(key, value);
            }
            return length;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long lpush(String key, List<String> values) {
        long result = lpush(key, values, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = lpush(key, values, clientSlaver);
        return result;
    }

    public String lpop(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.lpop(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public String lpop(String key) {
        String result = lpop(key, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = lpop(key, clientSlaver);
        return result;
    }

    public long srem(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.srem(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long srem(String key, String member) {
        long result = srem(key, member, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = srem(key, member, clientSlaver);
        return result;
    }

    public long srem(String key, List<String> members, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<String> memIte = members.iterator();
            while (memIte.hasNext()) {
                String member = memIte.next();
                count += jedis.srem(key, member);
            }
            return count;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long srem(String key, List<String> members) {
        long result = srem(key, members, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = srem(key, members, clientSlaver);
        return result;
    }

    public Set<String> smembers(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.smembers(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> smembers(String key) {
        return this.smembers(key, clientMaster);
    }

    public boolean sismember(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.sismember(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public boolean sismember(String key, String member) {
        return this.sismember(key, member, clientMaster);
    }

    public long expire(String key, int seconds, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.expire(key, seconds);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long del(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.del(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long del(List<String> keys, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<String> keyIte = keys.iterator();
            while (keyIte.hasNext()) {
                String key = keyIte.next();
                count += jedis.del(key);
            }
            return count;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long scard(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.scard(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long scard(String key) {
        return this.scard(key, clientMaster);
    }

    public long sadd(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.sadd(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long sadd(String key, String member) {
        long result = this.sadd(key, member, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = sadd(key, member, clientSlaver);
        return result;

    }

    public long sadd(String key, List<String> members, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<String> memIte = members.iterator();
            while (memIte.hasNext()) {
                String member = memIte.next();
                count += jedis.sadd(key, member);
            }
            return count;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long sadd(String key, List<String> members) {
        long result = this.sadd(key, members, clientMaster);
        if (bean.isFailover() && clientSlaver != null)
            result = sadd(key, members, clientSlaver);
        return result;
    }

    public ShardedJedisPool getMasterClient() {
        return clientMaster;
    }

    public ShardedJedisPool getSlaverClient() {
        return clientSlaver;
    }
}
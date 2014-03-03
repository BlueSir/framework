package com.sohu.smc.common.lifecycle;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.sql.SQLException;
import java.util.*;

/**
 * mongodb
 * User: qinqd
 * Date: 12-4-10
 * Time: 上午11:40
 * To change this template use File | Settings | File Templates.
 */
public class RedisClient implements Managed {
    private static final Logger LOGGER = Logger.getLogger(RedisClient.class);

    private final JedisPool[] pool ;
    public RedisClient(String conn) throws Exception{
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(1000);
        config.setTestOnBorrow(true);

        String[] host = conn.split(",");
        pool = new JedisPool[host.length];
        for (int i =0;i<host.length;i++){
            String[] host_ip = host[i].split(":");
            pool[i] = new JedisPool(config, host_ip[0], Integer.parseInt(host_ip[1]), 5000);
        }
    }

    @Override
    public void start() throws Exception {
        // already started, man

    }

    @Override
    public void stop() throws Exception {

    }

    public void ping() throws SQLException {

    }

    public String get(String key){
        

        if(pool.length == 0){
            return get(pool[0],key);
        }else{
            return get(pool[Math.abs(key.hashCode()) % pool.length],key);
        }
    }

    public String get(JedisPool jedisPool,String key){
        Jedis jedis;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            System.err.println("Could not obtain redis connection from the pool");
            return null;
        }
        try {
            return jedis.get(key);
        } finally {
            jedisPool.returnResource(jedis);
        }

    }

    public void set(String key, String value){
        

        if(pool.length == 0){
            set(pool[0],key,value);
        }else{
            set(pool[Math.abs(key.hashCode()) % pool.length],key,value);
        }
    }

    public void set(JedisPool jedisPool,String key, String value){
        Jedis jedis;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            System.err.println("Could not obtain redis connection from the pool");
            return;
        }
        try {
            jedis.set(key, value);
            jedis.expire(key, 60*60);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public long incr(String key){
        

        if(pool.length == 0){
            return incr(pool[0],key);
        }else{
            return incr(pool[Math.abs(key.hashCode()) % pool.length],key);
        }
    }

    public long incr(JedisPool jedisPool,String key){
        Jedis jedis;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            System.err.println("Could not obtain redis connection from the pool");
            return 0;
        }
        try {
            return jedis.incr(key);
        } finally {
            jedisPool.returnResource(jedis);
        }

    }

    public long incrBy(String key,long value){
        

        if(pool.length == 0){
            return incrBy(pool[0],key,value);
        }else{
            return incrBy(pool[Math.abs(key.hashCode()) % pool.length],key,value);
        }
    }

    public static long incrBy(JedisPool jedisPool,String key,long value){
        Jedis jedis;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            System.err.println("Could not obtain redis connection from the pool");
            return 0;
        }
        try {
            return jedis.incrBy(key, value);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void remove(String key){
        

        if(pool.length == 0){
            remove(pool[0],key);
        }else{
            remove(pool[Math.abs(key.hashCode()) % pool.length],key);
        }
    }

    public void remove(JedisPool jedisPool,String key){
        Jedis jedis;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            System.err.println("Could not obtain redis connection from the pool");
            return;
        }
        try {
            jedis.del(key);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public long expire(String key, int expire){
        

        if(pool.length == 0){
            return expire(pool[0], key, expire);
        }else{
            return expire(pool[Math.abs(key.hashCode()) % pool.length], key, expire);
        }
    }

    public long expire(JedisPool jedisPool,String key, int expire){
        Jedis jedis;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            System.err.println("Could not obtain redis connection from the pool");
            return 0;
        }
        try {
            return jedis.expire(key,expire);
        }catch (Exception e){

        } finally {
            jedisPool.returnResource(jedis);
        }
        return 0;
    }

    public Map<String,String> mget(String ... keys){
        
        Map<String,String> ret = new HashMap<String,String>();

        if(pool.length == 0){
            List<String> _keys = Arrays.asList(keys);
            List<String> list = _mget(pool[0], _keys.toArray(new String[_keys.size()]));
            if(list!=null){
                for(int j = 0;j< list.size();j++){
                    if(list.get(j) != null)
                        ret.put(_keys.get(j),list.get(j));
                }
            }
        }else{
            String[] key = keys.clone();
            ArrayList<String>[] _key = new ArrayList[pool.length];
            for(int i=0; i<pool.length; i++){
                _key[i] = new ArrayList<String>();
            }
            for(String k : keys){
                _key[Math.abs(k.hashCode()) % pool.length].add(k);
            }

            for(int i=0; i<pool.length; i++){
                if(_key[i] == null || _key[i].size() == 0) continue;

                List<String> list = _mget(pool[i], _key[i].toArray(new String[_key[i].size()]));
                if(list!=null){
                    for(int j = 0;j< list.size();j++){
                        if(list.get(j) != null)
                            ret.put(_key[i].get(j),list.get(j));
                    }

                }
            }
        }
        return ret;
    }

    private List<String> _mget(JedisPool jedisPool,String ... keys){
        Jedis jedis;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            System.err.println("Could not obtain redis connection from the pool");
            return null;
        }
        try {
            return jedis.mget(keys);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }
}

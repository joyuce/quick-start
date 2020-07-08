package com.j.openproject.redis;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Joyuce
 * @Type RedisTool
 * @Desc 封装redis
 * @date 2019年11月22日
 * @Version V1.0
 */
@Slf4j
@Component
public class RedisTool {

    @Autowired
    private JedisPool jedisPool;

    private final String LOCK_SUCCESS = "OK";
    private final String SET_IF_NOT_EXIST = "NX";
    private final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;
    /**
     * LUA分布式锁删除脚本
     */
    private final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 默认分布式锁过期时间 15分钟
     */
    private final Integer DEFAULT_LOCK_TIMEOUT = 15 * 60 * 1000;
    /**
     * 默认分布式锁存放 db
     */
    private final Integer DB_DEFAULT_LOCK = 1;

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识 uuid
     * @param timeout   超期时间 毫秒
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, int timeout) {
        boolean result = false;
        Jedis jedis = getJedis(DB_DEFAULT_LOCK);
        try {
            String code = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, timeout);
            if (LOCK_SUCCESS.equals(code)) {
                result = true;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 尝试获取分布式锁 (默认过期时间 15分钟)
     *
     * @param lockKey
     * @param requestId
     * @return
     */
    public boolean tryLock(String lockKey, String requestId) {
        return tryLock(lockKey, requestId, DEFAULT_LOCK_TIMEOUT);
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        boolean result = false;
        Jedis jedis = getJedis(DB_DEFAULT_LOCK);
        try {
            Object line = jedis.eval(RELEASE_LOCK_SCRIPT, Collections.singletonList(lockKey),
                                     Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(line)) {
                result = true;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    private Jedis getJedis(int db) {
        Jedis jedis = null;
        if (db < 0) {
            db = 0;
        }
        try {
            jedis = jedisPool.getResource();
            jedis.select(db);
        } catch (Exception e) {
            log.error("获取redis异常", e);
            if (jedis != null) {
                jedis.close();
            }
        }
        return jedis;
    }

    /**
     * 清空redis所有库
     */
    public void flushAll() {
        Jedis jedis = getJedis(0);
        try {
            String code = jedis.flushAll();
            log.info("执行了清空redis所有库操作，返回码:" + code);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @param timeout 过期时间 默认秒
     */
    public void setValue(String key, String value, int timeout) {
        setValue(key, value, timeout, 0);
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @param timeout 过期时间 默认秒
     */
    public void setValue(String key, String value, int timeout, int db) {
        Jedis jedis = getJedis(db);
        try {
            jedis.set(key, value);
            jedis.expire(key, timeout);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void delete(String key) {
        delete(key, 0);
    }

    public void delete(String key, int db) {
        Jedis jedis = getJedis(db);
        try {
            jedis.del(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        return getValue(key, 0);
    }

    public String getValue(String key, int db) {
        String value = null;
        Jedis jedis = getJedis(db);
        try {
            value = jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * bitmap
     *
     * @param key
     * @param offset
     * @param value
     * @param db
     * @return
     */
    public boolean setBit(String key, long offset, boolean value, int db) {
        boolean rs = false;
        Jedis jedis = getJedis(db);
        try {
            Boolean r = jedis.setbit(key, offset, value);
            if (r != null) {
                rs = r;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return rs;
    }

    /**
     * bitmap
     *
     * @param key
     * @param offset
     * @param value
     * @return
     */
    public boolean setBit(String key, long offset, boolean value) {
        return setBit(key, offset, value, 0);
    }

}

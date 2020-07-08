package com.j.openproject.cache;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type LocalCacheService
 * @Desc 本地缓存服务
 * @date 2020年03月10日
 * @Version V1.0
 */
@Slf4j
public class LocalCache {

    private static Cache<String, Cache> cacheManager = Caffeine.newBuilder().build();

    private final static String DEFAULT_CACHE = "default";

    private LocalCache() {

    }

    /**
     * 获取cache 默认10分钟过期 最大数量 20000
     *
     * @param cacheName
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Cache<K, V> getCache(String cacheName) {
        Cache c = cacheManager.getIfPresent(cacheName);
        if (c == null) {
            c = Caffeine.newBuilder().expireAfterAccess(600, TimeUnit.SECONDS).maximumSize(20000).build();
            cacheManager.put(cacheName, c);
        }
        return c;
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public static <K, V> V getValue(K key) {
        return getValue(DEFAULT_CACHE, key);
    }

    /**
     * 获取缓存
     *
     * @param cacheName
     * @param key
     * @return
     */
    public static <K, V> V getValue(String cacheName, K key) {
        if (cacheName == null || key == null) {
            return null;
        }
        Cache<K, V> cache = getCacheByName(cacheName);
        if (cache != null) {
            return cache.getIfPresent(key);
        }
        return null;
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    public static <K, V> void putValue(K key, V value) {
        putValue(DEFAULT_CACHE, key, value);
    }

    /**
     * 设置缓存
     *
     * @param cacheName
     * @param key
     * @param value
     */
    public static <K, V> void putValue(String cacheName, K key, V value) {
        if (cacheName == null || key == null || value == null) {
            return;
        }
        Cache<K, V> cache = getCacheByName(cacheName);
        if (cache == null) {
            cache = Caffeine.newBuilder().expireAfterAccess(600, TimeUnit.SECONDS).maximumSize(20000).build();
            cacheManager.put(cacheName, cache);
        }
        cache.put(key, value);
    }

    /**
     * 获取cache实例
     *
     * @param cacheName
     * @param <K>
     * @param <V>
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <K, V> Cache<K, V> getCacheByName(String cacheName){
        Cache<K, V> cache = cacheManager.getIfPresent(cacheName);
        return cache;
    }

}

package com.j.openproject.bitmap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.roaringbitmap.RoaringBitmap;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.j.openproject.cache.LocalCache;

/**
 * @author Joyuce
 * @Type LocalBitMapService
 * @Desc 本机bitmap服务
 * @date 2020年03月11日
 * @Version V1.0
 */
public class LocalBitMap {

    private Cache<String, RoaringBitmap> cache = LocalCache.getCache("bitmap");

    private Map<String, RoaringBitmap> map = new ConcurrentHashMap<>();

    private LocalBitMap() {

    }


    public static void main(String[] args) {

        RoaringBitmap r1 = new RoaringBitmap();
        for (int k = 4000; k < 4255; ++k) {
            r1.add(k);
        }

        RoaringBitmap r2 = new RoaringBitmap();
        for (int k = 1000; k < 4255; k += 2) {
            r2.add(k);
        }

        RoaringBitmap union = RoaringBitmap.or(r1, r2);
        RoaringBitmap intersection = RoaringBitmap.and(r1, r2);
        System.out.println(union.toString());

        String str = JSONObject.toJSONString(union);
        System.err.println(str);

        RoaringBitmap copy = JSONObject.parseObject(str, RoaringBitmap.class);
        System.out.println(copy.toString());
    }

    /**
     * 获取缓存会过期的bitmap
     *
     * @param key
     * @return
     */
    public RoaringBitmap getExpBitMap(String key) {
        RoaringBitmap bitmap = cache.getIfPresent(key);
        if (bitmap == null) {
            bitmap = new RoaringBitmap();
            cache.put(key, bitmap);
        }
        return bitmap;
    }

    /**
     * 获取不会过期的bitmap
     *
     * @param key
     * @return
     */
    public RoaringBitmap getBitMap(String key) {
        RoaringBitmap bitmap = map.get(key);
        if (bitmap == null) {
            bitmap = new RoaringBitmap();
            map.put(key, bitmap);
        }
        return bitmap;
    }

    /**
     * 删除bitmap
     *
     * @param key
     * @return
     */
    public boolean removeBitMap(String key) {
        RoaringBitmap bitmap = map.remove(key);
        return bitmap != null;
    }

}

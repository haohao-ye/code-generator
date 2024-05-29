package com.dkhaohao.web.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author dkhaohao
 * @Date 2024/5/28/17:39
 * @Description :
 */
@Component
public class CacheManager {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //本地缓存
    Cache<String, String> localCache = Caffeine.newBuilder()
            .expireAfterWrite(20, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /**
     * 写缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, String value){
        localCache.put(key, value);
        stringRedisTemplate.opsForValue().set(key, value, 20, TimeUnit.MINUTES);
    }

    public String get(String key) {
        //先从本地缓存中获取
        String value = localCache.getIfPresent(key);
        if (value != null) {
            return value;
        }

        //本地缓存未命中，从redis中获取
        value = stringRedisTemplate.opsForValue().get(key);
        if (value != null) {
            //将redis中的值放入本地缓存
            localCache.put(key, value);
        }
        return value;
    }

    //清除缓存

    /**
     * 删除缓存
     * @param key
     */
    public void delete(String key) {
        localCache.invalidate(key);
        stringRedisTemplate.delete(key);
    }


}

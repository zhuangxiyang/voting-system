package com.vote.common.redis.util;

import com.alibaba.fastjson.JSON;
import com.vote.common.redis.dto.RedisQueryInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    public ValueOperations<String, Object> opsForValue() {
        return redisTemplate.opsForValue();
    }

    /**
     * 根据key获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 将值放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void update(String key, Object value) {
        del(key);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将值放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (!StringUtils.isEmpty(key) && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
                return;
            }
            redisTemplate.delete(CollectionUtils.arrayToList(key));
        }
    }

    /**
     * 模糊删除缓存
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void delLeftLike(String key) {
        if (!StringUtils.isEmpty(key)) {
            redisTemplate.delete(redisTemplate.keys(key + "*"));
        }
    }

    /**
     * 模糊查询缓存
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public List<RedisQueryInfo> queryLike(String key) {
        List<RedisQueryInfo> list = new ArrayList<>();
        if (!StringUtils.isEmpty(key)) {
            Set<String> keys = redisTemplate.keys(key + "*");
            if (!CollectionUtils.isEmpty(keys)) {
                for (String o : keys) {
                    RedisQueryInfo r = new RedisQueryInfo();
                    r.setRedisKey(o);
                    list.add(r);
                }
                List multiGet = redisTemplate.opsForValue().multiGet(keys);
                for (int i = 0; i < multiGet.size(); i++) {
                    RedisQueryInfo o = list.get(i);
                    o.setRedisValue(JSON.toJSONString(multiGet.get(i)));
                }
            }
        }
        return list;
    }


    /**
     * 将值放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value, long time, TimeUnit unit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, unit);
            } else {
                set(key, value);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    /**
     * 将值放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value, long time, TimeUnit unit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, unit);
            } else {
                set(key, value);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    /**
     * 指定key过期时间
     * @param key
     * @param date
     * @return
     */
    public boolean expireAt(String key, Date date){
        return redisTemplate.expireAt(key, date);
    }
}

package com.vote.server.listener.redis;

import com.vote.common.redis.util.RedisUtil;
import com.vote.common.util.Constant;
import com.vote.server.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ActivityService activityService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("收到了消息" + message.toString() + "当前时间" + System.currentTimeMillis());
        String expiredKey = message.toString();
        //往redis插入1条数据，防止多台服务器同时消费
        String oldLock = getAndSet(expiredKey + ".lock", "1");
        if (StringUtils.isNotEmpty(oldLock) && "1".equals(oldLock)) {
            return;
        }
        try {
            //投票时间到，自动结束投票
            if (expiredKey.contains(Constant.ACT_REDIS_KEY)) {
                String[] params = expiredKey.split(Constant.ACT_REDIS_KEY);
                if (params.length > 1 && StringUtils.isNotEmpty(params[1])) {
                    activityService.activityOver(Arrays.asList(params[1]));
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            //删除redis
            redisUtil.del(expiredKey,expiredKey + ".lock");
        }
    }

    public  <T> T getAndSet(final String key, T value) {
        T oldValue = null;
        try {
            ValueOperations<String, Object> operations = redisUtil.opsForValue();
            oldValue =(T) operations.getAndSet(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldValue;
    }
}

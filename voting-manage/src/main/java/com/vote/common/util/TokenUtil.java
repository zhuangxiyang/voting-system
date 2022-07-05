package com.vote.common.util;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vote.common.dto.BaseUser;
import com.vote.common.dto.TokenEntity;
import com.vote.common.exception.BaseAssert;
import com.vote.common.exception.BaseException;
import com.vote.common.exception.TokenExpireException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * token控制工具类
 *
 */
@Component
@Slf4j
public class TokenUtil implements Serializable {

    private static final long serialVersionUID = 8617969696670516L;

    public static final String KEYPER = "token:";

    private static RedisTemplate<String, TokenEntity> tokenEntityRedisTemplate;
    @Autowired
    private void setHost(RedisTemplate<String, TokenEntity> tokenEntityRedisTemplate) {
        this.tokenEntityRedisTemplate = tokenEntityRedisTemplate;
    }

    public static Map<String,Object> getRequestHeaderParam(String... params){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (attributes == null) {
            throw new BaseException("获取不到当前请求");
        }
        HttpServletRequest request = attributes.getRequest();
        Map<String,Object> resultMap = new HashMap<>();
        if (params!=null && params.length>0) {
            for (String key: params) {
                resultMap.put(key, request.getHeader(key));
            }
        }
        return resultMap;
    }

    /**
     * 获取用户
     * @return
     */
    public static BaseUser getUser(){
        Map<String, Object> requestHeaderParam = getRequestHeaderParam(Constant.AUTHORIZATION);
        String token = (String) requestHeaderParam.get(Constant.AUTHORIZATION);
        if(EmptyUtils.isEmpty(token)){
            BaseUser baseUser = new BaseUser();
            baseUser.setUserCode("admin");
            return baseUser;
        }
        token = token.replace("Bearer ", "");
        return getBaseUserByToken(token);

    }
    /**
     * 根据token获取用户
     * @param token
     * @return
     */
    public static BaseUser getBaseUserByToken(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            Map<String,Object> map = jwt.getClaim(Constant.USER_INFO).asMap();
            return JSONObject.parseObject(JSONObject.toJSONString(map)).toJavaObject(BaseUser.class);
        }catch (Exception e){
            log.error("解析token失败:"+e.getMessage());
            throw new BaseException("token错误：");
        }
    }


    /**
     * 存储token
     * @param key
     * @param token
     * @return
     */
    public static Boolean pushToken(String key, String token, Date invalid, Integer max){
        String id = KEYPER+key;
        LocalDateTime invalidDate = invalid.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long size = tokenEntityRedisTemplate.opsForList().size(id);
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setInvalidDate(invalidDate);
        tokenEntity.setToken(token);
        if(size<=0){
            tokenEntityRedisTemplate.opsForList().rightPush(id,tokenEntity);
        }else{
            List<TokenEntity> tokenEntities = tokenEntityRedisTemplate.opsForList().range(id, 0, size);
            tokenEntities = tokenEntities.stream().filter(te -> te.getInvalidDate().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            if(tokenEntities.size()>= max){
                return false;
            }
            tokenEntities.add(tokenEntity);
            tokenEntityRedisTemplate.delete(id);
            tokenEntities.forEach(te->{
                tokenEntityRedisTemplate.opsForList().rightPush(id,te);
            });
        }
        return true;
    }

    /**
     * 判断token是否有效
     * @param key
     * @param token
     * @return true 有效 false: 无效
     */
    public static Boolean judgeTokenValid(String key, String token){
        String id = KEYPER+key;
        long size = tokenEntityRedisTemplate.opsForList().size(id);
        if(size<=0){
            throw new BaseException("token过期，请重新登录", -98);
        }else{
            List<TokenEntity> tokenEntities = tokenEntityRedisTemplate.opsForList().range(id, 0, size);
            tokenEntities = tokenEntities.stream().filter(te->te.getToken().equals(token)).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(tokenEntities)){
                throw new BaseException("token过期，请重新登录", -98);
            }
            TokenEntity tokenEntity = tokenEntities.get(0);
            if(tokenEntity.getInvalidDate().isAfter(LocalDateTime.now())&&tokenEntity.getStatus()==1){
                return true;
            }
        }
        throw new BaseException("token过期，请重新登录", -98);
    }

    /**
     * 登出
     * @param key
     * @param token
     */
    public static void logout(String key, String token){
        String id = KEYPER+key;
        long size = tokenEntityRedisTemplate.opsForList().size(id);
        if(size<=0){
            tokenEntityRedisTemplate.delete(id);
        }else{
            List<TokenEntity> tokenEntities = tokenEntityRedisTemplate.opsForList().range(id, 0, size);
            tokenEntities = tokenEntities.stream().filter(te->!te.getToken().equals(token)).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(tokenEntities)){
                tokenEntityRedisTemplate.delete(id);
            }
            tokenEntityRedisTemplate.delete(id);
            tokenEntities.forEach(te->{
                tokenEntityRedisTemplate.opsForList().rightPush(id,te);
            });
        }
    }
}

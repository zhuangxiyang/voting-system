package com.vote.common.redis.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RedisQueryInfo {
    @NotNull(message = "redisKey不能为空")
    private String redisKey;
    private String redisValue;

}

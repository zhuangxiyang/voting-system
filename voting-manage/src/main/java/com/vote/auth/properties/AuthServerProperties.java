package com.vote.auth.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.io.Serializable;

/**
 * 认证服务器配置
 *
 */
@Data
@ConfigurationProperties(prefix = "vote.auth.server")
public class AuthServerProperties  implements Serializable {
    /**
     * 最大登录次数
     */
    private Integer maxClient;
    /**
     * 最大有效时间，单位毫秒
     */
    private Integer tokenValid;
    /**
     * 是否允许强行登录
     */
    private Boolean force;
    /**
     * 是否开启刷新token
     */
    private Boolean startRefresh;
    /**
     * 刷新token有效时间
     */
    private Integer refreshTokenValid;
    /**
     * 路径
     */
    private Resource keyPath;
    /**
     * 别名
     */
    private String alias;
    /**
     * 密码
     */
    private String secret;
}

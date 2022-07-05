package com.vote.common.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 基础用户
 *
 */
@Data
public class BaseUser implements Serializable {
    /**
     * 主键Id
     */
    protected Long id;

    /**
     * 数据创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createDate = LocalDateTime.now();
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 账号
     */
    private String userCode;
    /**
     * 邮箱，用户企业人员进行登录
     */
    private String email;
    /**
     * 电话号码，用户客户登录
     */
    private String telephone;
    /**
     * 头像
     */
    private String headerUrl;
    /**
     * 组织编码
     */
    private String stationCode;
    /**
     * 密码过期时间
     */
    private Date passwordExpireTime;

    private String companyCode;

    private String companyName;

    private boolean isSuperAdmin;

    private String registerType;

}

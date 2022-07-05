package com.vote.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vote.common.dto.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 用户
 * </p>
 *
 * @author vote
 * @since 2022-07-02
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_vote_user")
@ApiModel(value = "VoteUser", description = "用户")
@AllArgsConstructor
public class VoteUser extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @TableField(value = "user_name", condition = LIKE)
    private String userName;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    @TableField(value = "user_code", condition = LIKE)
    private String userCode;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @TableField(value = "email", condition = LIKE)
    private String email;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @TableField(value = "telephone", condition = LIKE)
    private String telephone;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @TableField(value = "password", condition = LIKE)
    private String password;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @TableField(value = "header_url", condition = LIKE)
    private String headerUrl;

    /**
     * 启用状态
     */
    @ApiModelProperty(value = "启用状态")
    @TableField("login_status")
    private Integer loginStatus;

    /**
     * 组织编码
     */
    @ApiModelProperty(value = "组织编码")
    @TableField(value = "station_code", condition = LIKE)
    private String stationCode;

    /**
     * 密码过期时间
     */
    @ApiModelProperty(value = "密码过期时间")
    @TableField("password_expire_time")
    private Date passwordExpireTime;


    @Builder
    public VoteUser(Long id, String createUser, String updateUser, Date createTime, Date updateTime, Integer delFlag, 
                    String userName, String userCode, String email, String telephone, String password, 
                    String headerUrl, Integer loginStatus, String stationCode, Date passwordExpireTime) {
        this.id = id;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.delFlag = delFlag;
        this.userName = userName;
        this.userCode = userCode;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.headerUrl = headerUrl;
        this.loginStatus = loginStatus;
        this.stationCode = stationCode;
        this.passwordExpireTime = passwordExpireTime;
    }

}

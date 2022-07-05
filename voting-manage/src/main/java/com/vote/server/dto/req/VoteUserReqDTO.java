package com.vote.server.dto.req;

import com.vote.common.dto.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "VoteUserReqDTO", description = "用户")
public class VoteUserReqDTO extends Entity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String userName;
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String userCode;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String telephone;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;
    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String headerUrl;
    /**
     * 启用状态
     */
    @ApiModelProperty(value = "启用状态")
    private Integer loginStatus;
    /**
     * 组织编码
     */
    @ApiModelProperty(value = "组织编码")
    private String stationCode;
    /**
     * 密码过期时间
     */
    @ApiModelProperty(value = "密码过期时间")
    private Date passwordExpireTime;

}

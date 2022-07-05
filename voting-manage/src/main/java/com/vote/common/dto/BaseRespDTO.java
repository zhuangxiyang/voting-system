package com.vote.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体
 *
 * @date 2019/05/05
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class BaseRespDTO implements Serializable {

    @ApiModelProperty(value = "主键id")
    protected Long id;

    @ApiModelProperty(value = "创建时间")
    protected Date createTime;

    @ApiModelProperty(value = "创建人ID")
    protected String createUser;

    @ApiModelProperty(value = "最后修改时间")
    protected Date updateTime;

    @ApiModelProperty(value = "最后修改人ID")
    protected String updateUser;

    @ApiModelProperty(value = "创建人")
    protected String createUserName;

    @ApiModelProperty(value = "最后修改人ID")
    protected String updateUserName;
}

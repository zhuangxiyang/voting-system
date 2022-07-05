package com.vote.common.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 基础实体
 *
 * @author vote
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class Entity<T> extends SuperEntity<T> {

    public static final String UPDATE_TIME = "updateTime";
    public static final String UPDATE_USER = "updateUser";
    public static final String UPDATE_USER_COLUMN = "update_user";
    public static final String UPDATE_TIME_COLUMN = "update_time";
    public static final String DEL_FLAG = "delFlag";
    public static final String DEL_FLAG_COLUMN = "del_flag";

    private static final long serialVersionUID = 5169873634279173683L;

    @ApiModelProperty(value = "最后修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected Date updateTime;

    @ApiModelProperty(value = "最后修改人ID")
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    protected String updateUser;

    @ApiModelProperty(value = "0-正常,1-已删除（逻辑删除标识）")
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    protected Integer delFlag;

    @ApiModelProperty(value = "最后修改人名称")
    @TableField(exist = false)
    protected String updateUserName;

    public Entity(T id, Date createTime, String createUser, Date updateTime, String updateUser, Integer delFlag, String createUserName, String updateUserName) {
        super(id, createTime, createUser, createUserName);
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.delFlag = delFlag;
        this.updateUserName = updateUserName;
    }

    public Entity() {
    }

}

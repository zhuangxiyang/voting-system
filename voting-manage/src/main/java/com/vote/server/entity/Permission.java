package com.vote.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vote.common.dto.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * 权限表
 */
@Data
@TableName("sys_permission")
@NoArgsConstructor
public class Permission extends Entity<Long> {

    private static final long serialVersionUID = -7559506107525485531L;

    @ApiModelProperty(value = "权限名称")
    @TableField(value = "auth_name", condition = LIKE)
    private String authName;

    @ApiModelProperty(value = "权限编码")
    @TableField(value = "auth_code", condition = LIKE)
    private String authCode;

    @ApiModelProperty(value = "请求地址")
    @TableField(value = "request_url", condition = LIKE)
    private String requestUrl;

    @ApiModelProperty(value = "状态")
    @TableField(value = "status", condition = LIKE)
    private Integer status;

    @ApiModelProperty(value = "父权限")
    @TableField(value = "parent_permission", condition = LIKE)
    private Long parentPermission;

    @ApiModelProperty(value = "排序")
    @TableField(value = "sort", condition = LIKE)
    private Integer sort;

    @ApiModelProperty(value = "是否已有权限", hidden = true)
    @TableField(exist = false)
    protected Boolean isCheck = false;

    @ApiModelProperty(value = "子节点", hidden = true)
    @TableField(exist = false)
    protected List<Permission> children;

    @ApiModelProperty(value = "按钮", hidden = true)
    @TableField(exist = false)
    protected List<Permission> resourceList;

    /**
     * 初始化子类
     */
    public void initChildren() {
        if (getChildren() == null) {
            this.setChildren(new ArrayList<>());
        }
    }
}

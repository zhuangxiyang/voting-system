package com.vote.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vote.common.dto.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 流水号管理
 */
@Data
@TableName("sys_serial_index")
@NoArgsConstructor
public class SysSerialIndex extends Entity<Long> {

    private static final long serialVersionUID = -506390770038493830L;

    @ApiModelProperty(value = "编码")
    @TableField("serial_code")
    private String serialCode;

    @ApiModelProperty(value = "前缀（自定义参数）")
    @TableField("serial_param")
    private String serialParam;

    @ApiModelProperty(value = "日期")
    @TableField("serial_date")
    private String serialDate;

    @ApiModelProperty(value = "流水位数")
    @TableField("serial_num_count")
    private String serialNumCount;

    @ApiModelProperty(value = "当前流水号")
    @TableField("serial_index")
    private Integer serialIndex;

    @ApiModelProperty(value = "版本号")
    @TableField("version")
    private Integer version;

    @Builder
    public SysSerialIndex(Long id, String createUser, String updateUser, Date createTime, Date updateTime, Integer delFlag,
                          String serialCode, String serialParam, String serialDate, String serialNumCount, Integer serialIndex,
                          Integer version){
        this.id = id;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.delFlag = delFlag;
        this.serialCode = serialCode;
        this.serialParam = serialParam;
        this.serialDate = serialDate;
        this.serialNumCount = serialNumCount;
        this.serialIndex = serialIndex;
        this.version = version;
    }
}

package com.vote.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vote.common.dto.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流水号管理
 */
@Data
@TableName("sys_serial_number")
@NoArgsConstructor
public class SysSerialNumber extends Entity<Long> {

    private static final long serialVersionUID = 7001092391347704528L;

    @ApiModelProperty(value = "编码")
    @TableField("serial_code")
    private String serialCode;

    @ApiModelProperty(value = "名称")
    @TableField("serial_name")
    private String serialName;

    @ApiModelProperty(value = "流水号总数")
    @TableField("serial_num")
    private Long serialNum;

    @ApiModelProperty(value = "规则")
    @TableField("serial_rule")
    private String serialRule;

    @ApiModelProperty(value = "流水位数")
    @TableField("serial_num_count")
    private Integer serialNumCount;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "0-关闭,1-启用")
    @TableField("status")
    private Integer status;

}

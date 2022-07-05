package com.vote.server.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流水号管理
 */
@Data
@NoArgsConstructor
public class SerialNumberUpdateDTO {

    private static final long serialVersionUID = 7493319909657731490L;

    @ApiModelProperty(value = "编码")
    private String serialCode;

    @ApiModelProperty(value = "名称")
    private String serialName;

    @ApiModelProperty(value = "流水号总数")
    private Long serialNum;

    @ApiModelProperty(value = "规则")
    private String serialRule;

    @ApiModelProperty(value = "流水位数")
    private Integer serialNumCount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "0-关闭,1-启用")
    private Integer status;

}

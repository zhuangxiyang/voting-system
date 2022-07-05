package com.vote.server.dto.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 编码管理
 */
@Data
@ApiModel(value = "SerialNumberRequest", description = "编码管理")
public class SerialNumberRequest implements Serializable {

    private static final long serialVersionUID = -6115169553287735387L;

    /**
     * 编码
     */
    private String serialCode;

    /**
     * 数量
     */
    private Integer number;

}

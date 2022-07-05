package com.vote.server.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 编码管理
 */
@Data
public class SerialIndexDto implements Serializable {

    private static final long serialVersionUID = -5321677326449703363L;

    /**
     * 编码
     */
    private String serialCode;

    /**
     * 前缀（自定义参数）
     */
    private String serialParam;

    /**
     * 日期
     */
    private String serialDate;

    /**
     * 流水位数
     */
    private String serialNumCount;

}

package com.vote.common.dto;

/**
 * @author vote
 */
public interface BaseExceptionCode {
    /**
     * 异常编码
     *
     * @return
     */
    int getCode();

    /**
     * 异常消息
     *
     * @return
     */
    String getMsg();

    /**
     * 异常标识
     *
     * @return
     */
    String getErrorKey();

    /**
     * 占位符
     *
     * @return
     */
    Object[] getArgs();
}

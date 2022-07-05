package com.vote.common.exception;

/**
 * 自定义异常的父类
 *
 */
public class TokenAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = -8879123682017730252L;
    protected Integer code;

    public TokenAccessDeniedException(String message) {
        super(message);
    }

    public TokenAccessDeniedException(String message, int code) {
        super(message);
        this.code = code;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
package com.vote.common.exception;

/**
 * 权限相关异常
 *
 */
public class AuthException extends BaseException {


    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, int code) {
        super(message);
        this.code = code;
    }

    public AuthException(String message, int code, String title) {
        super(message);
        this.code = code;
        this.title = title;
    }
}

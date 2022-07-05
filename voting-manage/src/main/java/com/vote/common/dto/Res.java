package com.vote.common.dto;

import com.vote.common.exception.BaseException;
import com.vote.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@SuppressWarnings("ALL")
@Accessors(chain = true)
public class Res<T> {
    public static final String DEF_ERROR_MESSAGE = "系统繁忙，请稍候再试";
    public static final String HYSTRIX_ERROR_MESSAGE = "请求超时，请稍候再试";
    public static final int SUCCESS_CODE = 0;
    public static final int FAIL_CODE = -1;
    public static final int TIMEOUT_CODE = -2;
    /**
     * 统一参数验证异常
     */
    public static final int VALID_EX_CODE = -9;
    public static final int OPERATION_EX_CODE = -10;
    /**
     * 调用是否成功标识，0：成功，-1:系统繁忙，此时请开发者稍候再试 详情见[ExceptionCode]
     */
    @ApiModelProperty(value = "响应编码:0/200-请求处理成功")
    private int code;

    /**
     * 是否执行默认操作
     */
//    @JsonIgnore
    private Boolean defExec = false;

    /**
     * 调用结果
     */
    @ApiModelProperty(value = "响应数据")
    private T result;

    /**
     * 结果消息，如果调用成功，消息通常为空T
     */
    @ApiModelProperty(value = "提示消息")
    private String message = "ok";

    @ApiModelProperty(value = "请求路径")
    private String path;
    /**
     * 附加数据
     */
    @ApiModelProperty(value = "附加数据")
    private Map<Object, Object> extra;

    /**
     * 响应时间
     */
    @ApiModelProperty(value = "响应时间戳")
    private String timestamp = DateUtils.getNowDateTime();

    private Res() {
        this.defExec = false;
        this.timestamp = DateUtils.getNowDateTime();
    }

    public Res(int code, T result, String message) {
        this.code = code;
        this.result = result;
        this.message = message;
        this.defExec = false;
        this.timestamp = DateUtils.getNowDateTime();
    }

    public Res(int code, T result, String message, boolean defExec) {
        this.code = code;
        this.result = result;
        this.message = message;
        this.defExec = defExec;
        this.timestamp = DateUtils.getNowDateTime();
    }

    public static <E> Res<E> result(int code, E data, String msg) {
        return new Res<>(code, data, msg);
    }

    /**
     * 请求成功消息
     *
     * @param data 结果
     * @return RPC调用结果
     */
    public static <E> Res<E> success(E data) {
        return new Res<>(SUCCESS_CODE, data, "ok",true);
    }

    public static Res<Boolean> success() {
        return new Res<>(SUCCESS_CODE, true, "ok");
    }


    public static <E> Res<E> successDef(E data) {
        return new Res<>(SUCCESS_CODE, data, "ok", true);
    }

    public static <E> Res<E> successDef() {
        return new Res<>(SUCCESS_CODE, null, "ok", true);
    }

    public static <E> Res<E> successDef(E data, String msg) {
        return new Res<>(SUCCESS_CODE, data, msg, true);
    }

    /**
     * 请求成功方法 ，data返回值，msg提示信息
     *
     * @param data 结果
     * @param msg  消息
     * @return RPC调用结果
     */
    public static <E> Res<E> success(E data, String msg) {
        return new Res<>(SUCCESS_CODE, data, msg);
    }

    /**
     * 请求失败消息
     *
     * @param msg
     * @return
     */
    public static <E> Res<E> fail(int code, String msg) {
        throw new BaseException((msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg, code);
    }

    public static <E> Res<E> fail(E data) {
        return new Res<>(FAIL_CODE, data, "fail", false);
    }

    public static <E> Res<E> fail(String msg) {
        return fail(OPERATION_EX_CODE, msg);
    }

    public static <E> Res<E> fail(BaseException exception) {
        if (exception == null) {
            return fail(DEF_ERROR_MESSAGE);
        }
        throw new BaseException(exception.getMessage(), exception.getCode());
    }

    /**
     * 请求失败消息，根据异常类型，获取不同的提供消息
     *
     * @param throwable 异常
     * @return RPC调用结果
     */
    public static <E> Res<E> fail(Throwable throwable) {
        return fail(FAIL_CODE, throwable != null ? throwable.getMessage() : DEF_ERROR_MESSAGE);
    }

    public static <E> Res<E> validFail(String msg) {
        return new Res<>(VALID_EX_CODE, null, (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg);
    }

    public static <E> Res<E> validFail(String msg, Object... args) {
        String message = (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg;
        return new Res<>(VALID_EX_CODE, null, String.format(message, args));
    }

    public static <E> Res<E> validFail(BaseExceptionCode exceptionCode) {
        return new Res<>(exceptionCode.getCode(), null,
                (exceptionCode.getMsg() == null || exceptionCode.getMsg().isEmpty()) ? DEF_ERROR_MESSAGE : exceptionCode.getMsg());
    }

    public static <E> Res<E> checkFail(int code, E data, String message) {
        return new Res<>(code, data, message, false);
    }

    public static <E> Res<E> timeout() {
        return fail(TIMEOUT_CODE, HYSTRIX_ERROR_MESSAGE);
    }


    public Res<T> put(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>(10);
        }
        this.extra.put(key, value);
        return this;
    }

    public Res<T> putAll(Map<Object, Object> extra) {
        if (this.extra == null) {
            this.extra = new HashMap<>(10);
        }
        this.extra.putAll(extra);
        return this;
    }

    /**
     * 逻辑处理是否成功
     *
     * @return 是否成功
     */
    public Boolean getIsSuccess() {
        return this.code == SUCCESS_CODE || this.code == 200;
    }

    /**
     * 逻辑处理是否失败
     *
     * @return
     */
    public Boolean getIsError() {
        return !getIsSuccess();
    }


}

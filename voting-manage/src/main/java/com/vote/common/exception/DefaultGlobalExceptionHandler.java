package com.vote.common.exception;

import cn.hutool.core.util.StrUtil;
import com.vote.common.dto.ExceptionCode;
import com.vote.common.dto.Res;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vote.common.dto.ExceptionCode.METHOD_NOT_ALLOWED;
import static com.vote.common.dto.ExceptionCode.REQUIRED_FILE_PARAM_EX;

/**
 * 全局异常统一处理
 */
@Slf4j
public abstract class DefaultGlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> baseException(BaseException ex, HttpServletRequest request) {
        log.warn("BaseException:", ex);

        String message = ex.getMessage();

        return Res.<Void>result(ex.getCode(), null, message).setPath(request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> httpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("HttpMessageNotReadableException:", ex);
        String message = ex.getMessage();
        if (StrUtil.containsAny(message, "Could not read document:")) {
            String msg = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, "Could not read document:", " at "));
            return Res.<Void>result(ExceptionCode.PARAM_EX.getCode(), null, msg).setPath(request.getRequestURI());
        }
        return Res.<Void>result(ExceptionCode.PARAM_EX.getCode(), null, ExceptionCode.PARAM_EX.getMsg()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> bindException(BindException ex, HttpServletRequest request) {
        log.warn("BindException:", ex);
        try {
            String msgs = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
            if (StrUtil.isNotEmpty(msgs)) {
                return Res.<Void>result(ExceptionCode.PARAM_EX.getCode(), null, msgs).setPath(request.getRequestURI());
            }
        } catch (Exception ignore) {
        }
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = ex.getFieldErrors();
        fieldErrors.forEach((oe) ->
                msg.append("参数:[").append(oe.getObjectName())
                        .append(".").append(oe.getField())
                        .append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.")
        );
        return Res.<Void>result(ExceptionCode.PARAM_EX.getCode(), null, msg.toString()).setPath(request.getRequestURI());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("MethodArgumentTypeMismatchException:", ex);
        String msg = "参数：[" + ex.getName() +
                "]的传入值：[" + ex.getValue() +
                "]与预期的字段类型：[" + Objects.requireNonNull(Objects.requireNonNull(ex.getRequiredType())).getName() + "]不匹配";
        return Res.<Void>result(ExceptionCode.PARAM_EX.getCode(), null, msg).setPath(request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> illegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.warn("IllegalStateException:", ex);
        return Res.<Void>result(ExceptionCode.ILLEGALA_ARGUMENT_EX.getCode(), null, ExceptionCode.ILLEGALA_ARGUMENT_EX.getMsg()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> missingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("MissingServletRequestParameterException:", ex);
        return Res.<Void>result(ExceptionCode.ILLEGALA_ARGUMENT_EX.getCode(), null, "缺少必须的[" + ex.getParameterType() + "]类型的参数[" + ex.getParameterName() + "]").setPath(request.getRequestURI());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Res<Void> nullPointerException(NullPointerException ex, HttpServletRequest request) {
        log.warn("NullPointerException:", ex);
        return Res.<Void>result(ExceptionCode.NULL_POINT_EX.getCode(), null, ExceptionCode.NULL_POINT_EX.getMsg()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> illegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("IllegalArgumentException:", ex);
        return Res.<Void>result(ExceptionCode.ILLEGALA_ARGUMENT_EX.getCode(), null, ExceptionCode.ILLEGALA_ARGUMENT_EX.getMsg()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.warn("HttpMediaTypeNotSupportedException:", ex);
        MediaType contentType = ex.getContentType();
        if (contentType != null) {
            return Res.<Void>result(ExceptionCode.MEDIA_TYPE_EX.getCode(), null, "请求类型(Content-Type)[" + contentType.toString() + "] 与实际接口的请求类型不匹配").setPath(request.getRequestURI());
        }
        return Res.<Void>result(ExceptionCode.MEDIA_TYPE_EX.getCode(), null, "无效的Content-Type类型").setPath(request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> missingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request) {
        log.warn("MissingServletRequestPartException:", ex);
        return Res.<Void>result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> servletException(ServletException ex, HttpServletRequest request) {
        log.warn("ServletException:", ex);
        String msg = "UT010016: Not a multi part request";
        if (msg.equalsIgnoreCase(ex.getMessage())) {
            return Res.<Void>result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg());
        }
        return Res.<Void>result(ExceptionCode.SYSTEM_BUSY.getCode(), null, ex.getMessage()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> multipartException(MultipartException ex, HttpServletRequest request) {
        log.warn("MultipartException:", ex);
        return Res.<Void>result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg()).setPath(request.getRequestURI());
    }

    /**
     * jsr 规范中的验证异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> constraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));

        ConstraintViolation<?> violation = violations.iterator().next();
        String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
        String message2 = String.format("%s:%s", path, violation.getMessage());

        return Res.<Void>result(ExceptionCode.BASE_VALID_PARAM.getCode(), null, message).setPath(request.getRequestURI());
    }

    /**
     * spring 封装的参数验证异常， 在conttoller中没有写result参数时，会进入
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("MethodArgumentNotValidException:", ex);
        String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return Res.<Void>result(ExceptionCode.BASE_VALID_PARAM.getCode(), null, message).setPath(request.getRequestURI());
    }

    /**
     * 其他异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> otherExceptionHandler(Exception ex, HttpServletRequest request) {
        log.warn("Exception:", ex);
        if (ex.getCause() instanceof BaseException) {
            return this.baseException((BaseException) ex.getCause(), request);
        }
        return Res.<Void>result(ExceptionCode.SYSTEM_BUSY.getCode(), null, ExceptionCode.SYSTEM_BUSY.getMsg()).setPath(request.getRequestURI());
    }


    /**
     * 返回状态码:405
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("HttpRequestMethodNotSupportedException:", ex);
        return Res.<Void>result(METHOD_NOT_ALLOWED.getCode(), null, METHOD_NOT_ALLOWED.getMsg()).setPath(request.getRequestURI());
    }


    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> persistenceException(PersistenceException ex, HttpServletRequest request) {
        log.warn("PersistenceException:", ex);
        if (ex.getCause() instanceof BaseException) {
            BaseException cause = (BaseException) ex.getCause();
            return Res.<Void>result(cause.getCode(), null, cause.getMessage());
        }
        return Res.<Void>result(ExceptionCode.SQL_EX.getCode(), null, ExceptionCode.SQL_EX.getMsg()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> myBatisSystemException(MyBatisSystemException ex, HttpServletRequest request) {
        log.warn("PersistenceException:", ex);
        if (ex.getCause() instanceof PersistenceException) {
            return this.persistenceException((PersistenceException) ex.getCause(), request);
        }
        return Res.<Void>result(ExceptionCode.SQL_EX.getCode(), null, ExceptionCode.SQL_EX.getMsg()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> sqlException(SQLException ex, HttpServletRequest request) {
        log.warn("SQLException:", ex);
        return Res.<Void>result(ExceptionCode.SQL_EX.getCode(), null, ExceptionCode.SQL_EX.getMsg()).setPath(request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Res<Void> dataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("DataIntegrityViolationException:", ex);
        return Res.<Void>result(ExceptionCode.SQL_EX.getCode(), null, ExceptionCode.SQL_EX.getMsg()).setPath(request.getRequestURI());
    }

}

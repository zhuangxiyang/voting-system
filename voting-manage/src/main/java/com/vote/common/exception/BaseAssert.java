package com.vote.common.exception;

import cn.hutool.core.util.ArrayUtil;

import java.time.LocalDateTime;
import java.util.Collection;


/**
 * 断言
 */
public class BaseAssert {
    private BaseAssert() {
    }

    public static void fail(String message, int code) {
        throw new BaseException(message,code);
    }

    public static void fail() {
        fail("参数验证异常", -9);
    }

    public static void fail(String message) {
        if (message == null || "".equals(message)) {
            message = "参数验证异常";
        }
        fail(message, -9);
    }


    /**
     * 断言条件为真。如果不是，它会抛出一个参数检测异常
     * BaseException
     * condition 被检查的条件
     */
    public static void isTrue(boolean condition, String exceptionMessage) {
        if (!condition) {
            fail(exceptionMessage);
        }
    }

    /**
     * 断言条件为真。如果不是，它会抛出一个参数检测异常
     * BaseException
     * condition 被检查的条件
     */
    public static void isTrue(boolean condition) {
        if (!condition) {
            fail();
        }
    }

    public static void isFalse(boolean condition, String exceptionMessage) {
        if (condition) {
            fail(exceptionMessage);
        }
    }


    /**
     * 断言检查这个对象不是 Null。 如果是null，抛出异常
     * BaseException
     * exceptionMessage 错误码
     * object 检查对象
     */
    public static void notNull(Object object, String exceptionMessage) {
        if (object == null) {
            fail(exceptionMessage);
        }
    }

    public static void notNull(Object object) {
        if (object == null) {
            fail();
        }
    }

    /**
     * 断言检查这个对象是 Null。 如果不是null，抛出异常
     * BaseException
     * exceptionMessage 错误码
     * object 检查对象
     */
    public static void isNull(Object object, String exceptionMessage) {
        if (object != null) {
            fail(exceptionMessage);
        }
    }


    /**
     * 断言集合不为空，如果为null或者empty，抛出异常
     * BaseException
     * exceptionMessage 错误码
     * collection 集合
     */
    public static void notEmpty(Collection<?> collection, String exceptionMessage) {
        if (collection == null || collection.isEmpty()) {
            fail(exceptionMessage);
        }
    }

    public static <T> void notEmpty(T[] array, String exceptionMessage) {
        if (ArrayUtil.hasNull(array)) {
            fail(exceptionMessage);
        }
    }

    /**
     * 断言字符串不为空，如果为null或者empty，用指定错误码抛出异常
     * {@link BaseException}
     *
     * @param exceptionMessage 错误码
     * @param value         字符串
     * @
     */
    public static void notEmpty(String value, String exceptionMessage) {
        if (value == null || value.isEmpty()) {
            fail(exceptionMessage);
        }
    }

    public static void notEmpty(String value) {
        if (value == null || value.isEmpty()) {
            fail();
        }
    }

    /**
     * 断言2个对象不是相等的。如果相等则抛出异常
     */
    public static void notEquals(Object unexpected, Object actual, String exceptionMessage) {
        if (unexpected == actual) {
            fail(exceptionMessage);
        }
        if (unexpected != null && unexpected.equals(actual)) {
            fail(exceptionMessage);
        }
    }

    /**
     * 断言2个字符串是否相等，如果不等抛出异常
     */
    public static void equals(String expected, String actual, String exceptionMessage) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        fail(exceptionMessage);
    }

    /**
     * 断言 expected 大于 actual
     */
    public static void gt(LocalDateTime expected, LocalDateTime actual, String exceptionMsgs) {
        if (expected == null || actual == null) {
            fail(exceptionMsgs);
        }
        if (expected.isAfter(actual)) {
            fail(exceptionMsgs);
        }
    }
}

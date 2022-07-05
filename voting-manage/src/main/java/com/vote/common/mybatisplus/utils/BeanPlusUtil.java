package com.vote.common.mybatisplus.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bean增强类工具类
 *
 * <p>
 * 把一个拥有对属性进行set和get方法的类，我们就可以称之为JavaBean。
 * </p>
 *
 * @author vote
 */
public class BeanPlusUtil extends BeanUtil {

    /**
     * 转换 list
     *
     * @param sourceList
     * @param destinationClass
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> List<T> toBeanList(Collection<E> sourceList, Class<T> destinationClass) {
        if (sourceList == null || sourceList.isEmpty() || destinationClass == null) {
            return Collections.emptyList();
        }
        return sourceList.parallelStream()
                .filter(Objects::nonNull)
                .map((source) -> toBean(source, destinationClass))
                .collect(Collectors.toList());
    }

    /**
     * 转化Page 对象
     *
     * @param page
     * @param destinationClass
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> IPage<T> toBeanPage(IPage<E> page, Class<T> destinationClass) {
        if (page == null || destinationClass == null) {
            return null;
        }
        IPage<T> newPage = new Page<>(page.getCurrent(), page.getSize());
        newPage.setPages(page.getPages());
        newPage.setTotal(page.getTotal());

        List<E> list = page.getRecords();
        if (CollUtil.isEmpty(list)) {
            return newPage;
        }

        List<T> destinationList = toBeanList(list, destinationClass);

        newPage.setRecords(destinationList);
        return newPage;
    }

    public static void copyNotNullProperties(Object source, Object target, String[] ignoreProperties) throws BeansException {
        copyNotNullProperties(source, target, (Class)null, ignoreProperties);
    }

    public static void copyNotNullProperties(Object source, Object target, Class<?> editable) throws BeansException {
        copyNotNullProperties(source, target, editable, (String[])null);
    }

    public static void copyNotNullProperties(Object source, Object target) throws BeansException {
        copyNotNullProperties(source, target, (Class)null, (String[])null);
    }

    private static void copyNotNullProperties(Object source, Object target, Class<?> editable, String[] ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }

            actualEditable = editable;
        }

        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
        PropertyDescriptor[] var7 = targetPds;
        int var8 = targetPds.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            PropertyDescriptor targetPd = var7[var9];
            if (targetPd.getWriteMethod() != null && (ignoreProperties == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }

                        Object value = readMethod.invoke(source);
                        boolean stringValueTypeFlag = "java.lang.String".equals(readMethod.getReturnType().getName());
                        if (value != null || stringValueTypeFlag) {
                            boolean isEmpty = false;
                            if (stringValueTypeFlag) {
                                if (value == null) {
                                    isEmpty = true;
//                                } else {
//                                    String stringValue = (String)value;
//                                    if ("".equals(stringValue.trim())) {
//                                        isEmpty = true;
//                                    }
                                }
                            }

                            if (value instanceof Set) {
                                Set<?> s = (Set)value;
                                if (s.isEmpty()) {
                                    isEmpty = true;
                                }
                            } else if (value instanceof Map) {
                                Map<?, ?> m = (Map)value;
                                if (m.isEmpty()) {
                                    isEmpty = true;
                                }
                            } else if (value instanceof List) {
                                List<?> l = (List)value;
                                if (l.size() < 1) {
                                    isEmpty = true;
                                }
                            } else if (value instanceof Collection) {
                                Collection<?> c = (Collection)value;
                                if (c.size() < 1) {
                                    isEmpty = true;
                                }
                            }

                            if (!isEmpty) {
                                Method writeMethod = targetPd.getWriteMethod();
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }

                                writeMethod.invoke(target, value);
                            }
                        }
                    } catch (Throwable var17) {
                        throw new FatalBeanException("Could not copy properties from source to target", var17);
                    }
                }
            }
        }

    }

    public static void copyNotNullPropertiesSimple(Object source, Object target, String[] ignoreProperties) throws BeansException {
        copyNotNullPropertiesSimple(source, target, (Class)null, ignoreProperties);
    }

    public static void copyNotNullPropertiesSimple(Object source, Object target, Class<?> editable) throws BeansException {
        copyNotNullPropertiesSimple(source, target, editable, (String[])null);
    }

    public static void copyNotNullPropertiesSimple(Object source, Object target) throws BeansException {
        copyNotNullPropertiesSimple(source, target, (Class)null, (String[])null);
    }

    private static void copyNotNullPropertiesSimple(Object source, Object target, Class<?> editable, String[] ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }

            actualEditable = editable;
        }

        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
        PropertyDescriptor[] var7 = targetPds;
        int var8 = targetPds.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            PropertyDescriptor targetPd = var7[var9];
            if (targetPd.getWriteMethod() != null && (ignoreProperties == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }

                        Object value = readMethod.invoke(source);
                        if (value != null) {
                            Method writeMethod = targetPd.getWriteMethod();
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }

                            writeMethod.invoke(target, value);
                        }
                    } catch (Throwable var15) {
                        throw new FatalBeanException("Could not copy properties from source to target", var15);
                    }
                }
            }
        }

    }
}

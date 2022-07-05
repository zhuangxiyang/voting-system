package com.vote.common.mybatisplus.mybatis.conditions;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.vote.common.util.DateUtils;
import com.vote.common.mybatisplus.annotation.SelectField;
import com.vote.common.mybatisplus.base.entity.RemoteData;
import com.vote.common.mybatisplus.mybatis.conditions.query.LbqWrapper;
import com.vote.common.mybatisplus.mybatis.conditions.query.QueryWrap;
import com.vote.common.mybatisplus.mybatis.conditions.update.LbuWrapper;
import com.vote.common.mybatisplus.utils.StrHelper;
import com.vote.common.mybatisplus.utils.StrPool;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrappers 工具类， 该方法的主要目的是为了 缩短代码长度
 *
 * @author vote
 */
public class Wraps {

    /**
     * 模糊匹配关键字（以xx结尾）
     */
    public static final String LIKE_KEYWORD = "Keyword";

    private Wraps() {
        // ignore
    }

    /**
     * 获取 QueryWrap&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return QueryWrapper&lt;T&gt;
     */
    public static <T> QueryWrap<T> q() {
        return new QueryWrap<>();
    }

    /**
     * 获取 QueryWrap&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return QueryWrapper&lt;T&gt;
     */
    public static <T> QueryWrap<T> q(T entity) {
        return new QueryWrap<>(entity);
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LbqWrapper<T> lbQ() {
        return new LbqWrapper<>();
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LbqWrapper<T> lbQ(T entity) {
        return new LbqWrapper<>(entity);
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param clazz 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LbqWrapper<T> lbQ(Class<T> clazz) {
        return new LbqWrapper<>(clazz);
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LbuWrapper<T> lbU() {
        return new LbuWrapper<>();
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> UpdateWrapper<T> u() {
        return new UpdateWrapper<>();
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LbuWrapper<T> lbU(T entity) {
        return new LbuWrapper<>(entity);
    }


    public static <Entity> LbqWrapper<Entity> lbq(Entity model, Map<String, String> map, Class<Entity> modelClazz) {
        return q(model, map, modelClazz).lambda();
    }

    /**
     * 获取类中以xx结尾的字段名列表
     * @param clazz
     * @param targetName
     * @return
     */
    public static List<String> getFieldNameEndsWith(Class clazz, String targetName) {
        List<String> fieldNameList = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
                String name = field.getName();
                if(name.endsWith(targetName)){
                    fieldNameList.add(name);
                }
        }
        return fieldNameList;
    }

    /**
     * 根据字段名获取属性值
     * @param object
     * @param fieldName
     * @return
     */
    public static String getFieldValueByFieldName(Object object,String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (String) field.get(object);
        } catch (Exception e) {
            return null;
        }
    }


    public static <Entity> QueryWrap<Entity> q(Entity model, Map<String, String> map, Class<Entity> modelClazz) {

        QueryWrap<Entity> wrapper = model != null ? Wraps.q(model) : Wraps.q();

        if(Arrays.stream(model.getClass().getDeclaredFields())
                .anyMatch(field ->Optional.ofNullable(field.getAnnotation(SelectField.class)).isPresent())){
            String needSelect =Arrays.stream(ReflectUtil.getFieldsDirectly(model.getClass(),true))
                    .map(field -> field.getAnnotation(SelectField.class))
                    .filter(f ->Optional.ofNullable(f).isPresent())
                    .map(tableField -> tableField.value())
                    .collect(Collectors.joining(",","",""));
            wrapper.select(needSelect);
        }
//        wrapper.select(needSelect);
        //匹配model中以 "LIKE_KEYWORD" 结尾的字段，拼接wraps
//        List<String> keyword = getFieldNameEndsWith(modelClazz, LIKE_KEYWORD);
//        if(!CollectionUtils.isEmpty(keyword)){
//            for (String k : keyword) {
//                wrapper.like(getDbField(StrUtil.subBefore(k, LIKE_KEYWORD, true), modelClazz),
//                    getFieldValueByFieldName(model, k));
//            }
//        }

        if (CollUtil.isNotEmpty(map)) {
            //拼装区间
            for (Map.Entry<String, String> field : map.entrySet()) {
                String key = field.getKey();
                String value = field.getValue();
                if (StrUtil.isEmpty(value)) {
                    continue;
                }
                if (key.endsWith("_keyword")) {
                    String beanField = StrUtil.subBefore(key, "_keyword", true);
                    wrapper.like(getDbField(beanField, modelClazz),value);
                }
                if (key.endsWith("_st")) {
                    String beanField = StrUtil.subBefore(key, "_st", true);
                    wrapper.ge(getDbField(beanField, modelClazz), DateUtils.getStartTime(value));
                }
                if (key.endsWith("_ed")) {
                    String beanField = StrUtil.subBefore(key, "_ed", true);
                    wrapper.le(getDbField(beanField, modelClazz), DateUtils.getEndTime(value));
                }

                if (key.endsWith("_start")) {
                    String beanField = StrUtil.subBefore(key, "_start", true);
                    wrapper.ge(getDbField(beanField, modelClazz), Long.valueOf(value));
                }
                if (key.endsWith("_end")) {
                    String beanField = StrUtil.subBefore(key, "_end", true);
                    wrapper.le(getDbField(beanField, modelClazz), Long.valueOf(value));
                }
                if (key.endsWith("_in")) {
                    String beanField = StrUtil.subBefore(key, "_in", true);
                    wrapper.in(getDbField(beanField, modelClazz), Lists.newArrayList(value.split(",")));
                }
            }
        }
        return wrapper;
    }


    /**
     * 根据 bean字段 反射出 数据库字段
     *
     * @param beanField
     * @param clazz
     * @return
     */
    public static String getDbField(String beanField, Class<?> clazz) {
        Field field = ReflectUtil.getField(clazz, beanField);
        if (field == null) {
            return StrUtil.EMPTY;
        }
        TableField tf = field.getAnnotation(TableField.class);
        if (tf != null && StrUtil.isNotEmpty(tf.value())) {
            return tf.value();
        }
        return StrUtil.EMPTY;
    }


    /**
     * 替换 实体对象中类型为String 类型的参数，并将% 和 _ 符号转义
     *
     * @param source 源对象
     * @return 最新源对象
     * @see
     */
    public static <T> T replace(Object source) {
        if (source == null) {
            return null;
        }
        Object target = source;

        Class<?> srcClass = source.getClass();
        Field[] fields = ReflectUtil.getFields(srcClass);
        for (Field field : fields) {
            Object classValue = ReflectUtil.getFieldValue(source, field);
            if (classValue == null) {
                continue;
            }
            //final 和 static 字段跳过
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }


            if (classValue instanceof RemoteData) {
                RemoteData rd = (RemoteData) classValue;
                Object key = rd.getKey();
                if (ObjectUtil.isEmpty(key)) {
                    ReflectUtil.setFieldValue(target, field, null);
                    continue;
                }
                if (!(key instanceof String)) {
                    continue;
                }
                String strKey = (String) key;
                if (strKey.contains(StrPool.PERCENT) || strKey.contains(StrPool.UNDERSCORE)) {
                    String tarValue = StrHelper.keywordConvert(strKey);
                    rd.setKey(tarValue);
                    ReflectUtil.setFieldValue(target, field, rd);
                }
                continue;
            }

            if (!(classValue instanceof String)) {
                continue;
            }
            String srcValue = (String) classValue;
            if (srcValue.contains(StrPool.PERCENT) || srcValue.contains(StrPool.UNDERSCORE)) {
                String tarValue = StrHelper.keywordConvert(srcValue);
                ReflectUtil.setFieldValue(target, field, tarValue);
            }
        }
        return (T) target;
    }


}

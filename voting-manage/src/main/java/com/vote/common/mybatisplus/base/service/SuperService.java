package com.vote.common.mybatisplus.base.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 基于MP的 IService 新增了2个方法： saveBatchSomeColumn、updateAllById
 * 其中：
 * 1，updateAllById 执行后，会清除缓存
 * 2，saveBatchSomeColumn 批量插入
 *
 * @param <T> 实体
 * @author vote
 */
public interface SuperService<T> extends IService<T> {
    /**
     * 获取实体的类型
     *
     * @return
     */
    Class<T> getEntityClass();

    /**
     * 新增自定义参数检查,初始化部分字段
     *
     * @param entity
     */
    default void validAndInitSaveParam(T entity) {
    }

    /**
     * 新增成功回调
     *
     * @param entity
     */
    default void saveSuccessCallback(T entity) {
    }

    /**
     * 更新自定义参数检查
     *
     * @param entity
     */
    default void validUpdateParam(T entity) {
    }

    /**
     * 更新成功回调
     *
     * @param entity
     */
    default void updateSuccessCallback(T entity) {
    }

    /**
     * 删除自定义参数检查
     *
     * @param codeList
     */
    default void validDeleteParam(List<String> codeList) {
    }
    /**
     * 删除成功回调
     *
     * @param codeList
     */
    default void deleteSuccessCallback(List<String> codeList) {
    }

    boolean saveWithOutToken(T model);

}

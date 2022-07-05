package com.vote.common.mybatisplus.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vote.common.dto.SuperEntity;

import java.io.Serializable;

/**
 * 基于MP的 BaseMapper 新增了2个方法： insertBatchSomeColumn、updateAllById
 *
 * @param <T> 实体
 * @author vote
 */
public interface SuperMapper<T extends SuperEntity<? extends Serializable>> extends BaseMapper<T> {


}

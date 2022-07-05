package com.vote.common.mybatisplus.base.service;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.vote.common.dto.Entity;
import com.vote.common.mybatisplus.base.mapper.SuperMapper;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * 基于SpringCache + J2Cache 实现的 缓存实现
 * key规则： CacheConfig#cacheNames:id
 * <p>
 * CacheConfig#cacheNames 会先从子类获取，子类没设置，就从SuperServiceCacheImpl类获取
 * <p>
 * 1，getByIdCache：新增的方法： 先查缓存，在查db
 * 2，removeById：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * 3，removeByIds：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * 4，updateAllById： 新增的方法： 修改数据（所有字段）后，淘汰缓存
 * 5，updateById：重写 ServiceImpl 类的方法，修改db后，淘汰缓存
 *
 * @param <M>
 * @param <T>
 * @author vote
 */
public abstract class SuperCacheServiceImpl<M extends SuperMapper<T>, T extends Entity<? extends Serializable>> extends SuperServiceImpl<M, T> implements SuperCacheService<T> {

    @Autowired
    protected CacheChannel cacheChannel;

    /**
     * 缓存的 region,
     * 这个值一定要全类型唯一，否则会跟其他缓存冲突
     * 记得重写该类！
     *
     * @return
     */
    protected abstract String getRegion();


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(T model) {
        boolean save = super.save(model);
        if (model != null) {
            String key = key(model.getId());
            cacheChannel.set(getRegion(), key, model);
        }
        return save;
    }

    // 以下方法还能优化成批量清理缓存和设置缓存-----------
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            sqlSession.insert(sqlStatement, entity);

            // 设置缓存
            if (entity != null) {
                String key = key(entity.getId());
                cacheChannel.set(getRegion(), key, entity);
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");

        BiPredicate<SqlSession, T> predicate = (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        };

        BiConsumer<SqlSession, T> consumer = (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);

            // 清理缓存
            if (entity != null) {
                String key = key(entity.getId());
                cacheChannel.evict(getRegion(), key);
            }
        };

        String sqlStatement = SqlHelper.getSqlStatement(this.mapperClass, SqlMethod.INSERT_ONE);
        return SqlHelper.executeBatch(entityClass, log, entityList, batchSize, (sqlSession, entity) -> {
            if (predicate.test(sqlSession, entity)) {
                sqlSession.insert(sqlStatement, entity);
                // 设置缓存
                if (entity != null) {
                    String key = key(entity.getId());
                    cacheChannel.set(getRegion(), key, entity);
                }
            } else {
                consumer.accept(sqlSession, entity);
            }
        });


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);

            // 清理缓存
            if (entity != null) {
                String key = key(entity.getId());
                cacheChannel.evict(getRegion(), key);
            }
        });
    }

    @Override
    public T getByKey(String region, String key, Function<String, Object> loader) {
        // 根据key 查询缓存，若缓存中没数据，则从loader中加载数据写入缓存
        CacheObject cacheObject = cacheChannel.get(region, key, loader);

        if (cacheObject.getValue() == null) {
            // 缓存和loader中均无数据，直接返回
            return (T) null;
        }
        // 根据id查询数据
        String code = (String) cacheObject.getValue();
        return getByCodeCache(code);
    }

    protected String getCode(T model) {
            return model.getId().toString();
    }

    protected void setCache(T model) {
        String code = model.getId().toString();
        if (code != null) {
            String key = key(code);
            cacheChannel.set(getRegion(), key, model);
        }
    }


    protected void delCache(T model) {
        String code = getCode(model);
        if (code != null) {
            String key = key(code);
            cacheChannel.evict(getRegion(), key);
        }
    }

    @Override
    public void refreshCache() {
        list().forEach(this::setCache);
    }

    @Override
    public void clearCache() {
        list().forEach(this::delCache);
    }
}

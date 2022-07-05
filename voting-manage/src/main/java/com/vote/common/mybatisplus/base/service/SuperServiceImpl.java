package com.vote.common.mybatisplus.base.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vote.common.dto.Entity;
import com.vote.common.dto.Res;
import com.vote.common.exception.BaseException;
import com.vote.common.util.BaseUserUtil;
import com.vote.common.util.EmptyUtils;
import com.vote.common.mybatisplus.base.constant.CoreConstant;
import com.vote.common.mybatisplus.base.mapper.SuperMapper;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.mybatis.conditions.Wraps;
import com.vote.common.mybatisplus.mybatis.conditions.query.QueryWrap;
import com.vote.common.mybatisplus.utils.StrPool;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Date;

/**
 * 不含缓存的Service实现
 * <p>
 * <p>
 * 2，removeById：重写 ServiceImpl 类的方法，删除db
 * 3，removeByIds：重写 ServiceImpl 类的方法，删除db
 * 4，updateAllById： 新增的方法： 修改数据（所有字段）
 * 5，updateById：重写 ServiceImpl 类的方法，修改db后
 *
 * @param <M> Mapper
 * @param <T> 实体
 * @author vote
 */
//@Slf4j
public class SuperServiceImpl<M extends SuperMapper<T>, T extends Entity<? extends Serializable>> extends ServiceImpl<M, T> implements SuperService<T> {

    protected Class<T> entityClass = null;

    public SuperMapper getSuperMapper() {
        if (baseMapper instanceof SuperMapper) {
            return baseMapper;
        }
        throw new BaseException("Mapper类转换异常",-11);
    }

    @Override
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        }
        return this.entityClass;
    }

    /**
     * 构建没有租户信息的key
     *
     * @param args
     * @return
     */
    protected static String buildKey(Object... args) {
        if (args.length == 1) {
            return String.valueOf(args[0]);
        } else if (args.length > 0) {
            return StrUtil.join(StrPool.COLON, args);
        } else {
            return "";
        }
    }

    /**
     * 构建key
     *
     * @param args
     * @return
     */
    protected String key(Object... args) {
        return buildKey(args);
    }

    @Override
    public boolean save(T model) {
        Res<Boolean> result = handlerSave(model);
        if (result.getDefExec()) {
            return super.save(model);
        }
        return result.getResult();
    }

    /**
     * 处理新增相关处理
     *
     * @param model
     * @return
     */
    protected Res<Boolean> handlerSave(T model) {
        model.setUpdateTime(new Date());
        if (EmptyUtils.isEmpty(model.getCreateTime())) {
            model.setCreateTime(new Date());
        }
        if(EmptyUtils.isEmpty(model.getCreateUser())){
            model.setCreateUser(BaseUserUtil.getCurrentUser().getUserCode());
        }
        model.setUpdateUser(BaseUserUtil.getCurrentUser().getUserCode());
        if(EmptyUtils.isEmpty(model.getDelFlag())){
            model.setDelFlag(CoreConstant.FLAG_NORMAL);
        }
        return Res.successDef();
    }

    @Override
    public boolean saveWithOutToken(T model) {
        Res<Boolean> result = handlerWithOutTokenSave(model);
        if (result.getDefExec()) {
            return super.save(model);
        }
        return result.getResult();
    }

    /**
     * 处理新增相关处理
     *
     * @param model
     * @return
     */
    protected Res<Boolean> handlerWithOutTokenSave(T model) {
        model.setUpdateTime(new Date());
        if (EmptyUtils.isEmpty(model.getCreateTime())) {
            model.setCreateTime(new Date());
        }
        if(EmptyUtils.isEmpty(model.getCreateUser())){
            model.setCreateUser("admin");
        }
        if(EmptyUtils.isEmpty(model.getUpdateUser())){
            model.setUpdateUser("admin");
        }
        if(EmptyUtils.isEmpty(model.getDelFlag())){
            model.setDelFlag(CoreConstant.FLAG_NORMAL);
        }
        return Res.successDef();
    }

    protected IPage<T> modelPage(PageParams pageParams){
        IPage<T> ipage = pageParams.buildPage();
        T model = BeanUtil.toBean(pageParams.getModel(), getEntityClass());
        QueryWrap<T> wrapper = Wraps.q(model, pageParams.getMap(), getEntityClass());
        //只查未删除的数据
        wrapper.eq(Entity.DEL_FLAG_COLUMN, CoreConstant.FLAG_NORMAL);
        page(ipage, wrapper);
        return ipage;
    }
}

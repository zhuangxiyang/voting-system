package com.vote.common.mybatisplus.datasource;


import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.vote.common.mybatisplus.injector.MySqlInjector;
import com.vote.common.mybatisplus.mybatis.typehandler.FullLikeTypeHandler;
import com.vote.common.mybatisplus.mybatis.typehandler.LeftLikeTypeHandler;
import com.vote.common.mybatisplus.mybatis.typehandler.RightLikeTypeHandler;
import com.vote.common.mybatisplus.mybatis.typehandler.WriteInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;


/**
 * Mybatis 常用重用拦截器，vote.database.multiTenantType=任意模式 都需要实例出来
 * <p>
 * 拦截器执行一定是：
 * WriteInterceptor > DataScopeInterceptor > PaginationInterceptor
 *
 * @author vote
 */
@Slf4j
public abstract class BaseMybatisConfiguration {
    protected final DatabaseProperties databaseProperties;

    public BaseMybatisConfiguration(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    /**
     * 演示环境权限拦截器
     *
     * @return
     */
    @Bean
    @Order(15)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "isNotWrite", havingValue = "true")
    public WriteInterceptor getWriteInterceptor() {
        return new WriteInterceptor();
    }


    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * <p>
     * 注意:
     * 如果内部插件都是使用,需要注意顺序关系,建议使用如下顺序
     * 多租户插件,动态表名插件
     * 分页插件,乐观锁插件
     * sql性能规范插件,防止全表更新与删除插件
     * 总结: 对sql进行单次改造的优先放入,不对sql进行改造的最后放入
     * <p>
     * 参考：
     * https://mybatis.plus/guide/interceptor.html#%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F-%E4%BB%A5%E5%88%86%E9%A1%B5%E6%8F%92%E4%BB%B6%E4%B8%BE%E4%BE%8B
     */
    @Bean
    @Order(5)
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        List<InnerInterceptor> beforeInnerInterceptor = getPaginationBeforeInnerInterceptor();
        if (!beforeInnerInterceptor.isEmpty()) {
            beforeInnerInterceptor.forEach(interceptor::addInnerInterceptor);
        }

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 单页分页条数限制
        paginationInterceptor.setMaxLimit(databaseProperties.getLimit());
        // 数据库类型
        paginationInterceptor.setDbType(databaseProperties.getDbType());
        // 溢出总页数后是否进行处理
        paginationInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInterceptor);


        List<InnerInterceptor> afterInnerInterceptor = getPaginationAfterInnerInterceptor();
        if (!afterInnerInterceptor.isEmpty()) {
            afterInnerInterceptor.forEach(interceptor::addInnerInterceptor);
        }

        //防止全表更新与删除插件
        if (databaseProperties.getIsBlockAttack()) {
            BlockAttackInnerInterceptor baii = new BlockAttackInnerInterceptor();
            interceptor.addInnerInterceptor(baii);
        }
        // sql性能规范插件
        if (databaseProperties.getIsIllegalSql()) {
            IllegalSQLInnerInterceptor isi = new IllegalSQLInnerInterceptor();
            interceptor.addInnerInterceptor(isi);
        }

        return interceptor;
    }


    /**
     * 分页拦截器之前的插件
     *
     * @return
     */
    protected List<InnerInterceptor> getPaginationAfterInnerInterceptor() {
        return Collections.emptyList();
    }

    /**
     * 分页拦截器之后的插件
     *
     * @return
     */
    protected List<InnerInterceptor> getPaginationBeforeInnerInterceptor() {
        return Collections.emptyList();
    }

    /**
     * mybatis-plus 3.4.0开始采用新的分页插件,一缓和二缓遵循mybatis的规则,
     * 需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题
     * (该属性会在旧插件移除后一同移除)
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

    /**
     * Mybatis Plus 注入器
     *
     * @return
     */
    @Bean("myMetaObjectHandler")
    @ConditionalOnMissingBean
    public MetaObjectHandler getMyMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }



    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=leftLike} 类型的参数
     * 用于左模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=leftLike}
     *
     * @return
     */
    @Bean
    public LeftLikeTypeHandler getLeftLikeTypeHandler() {
        return new LeftLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=rightLike} 类型的参数
     * 用于右模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=rightLike}
     *
     * @return
     */
    @Bean
    public RightLikeTypeHandler getRightLikeTypeHandler() {
        return new RightLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=fullLike} 类型的参数
     * 用于全模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=fullLike}
     *
     * @return
     */
    @Bean
    public FullLikeTypeHandler getFullLikeTypeHandler() {
        return new FullLikeTypeHandler();
    }


    @Bean
    @ConditionalOnMissingBean
    public MySqlInjector getMySqlInjector() {
        return new MySqlInjector();
    }

    /**
     * gateway 网关模块需要禁用 spring-webmvc 相关配置，必须通过在类上面加限制条件方式来实现， 不能直接Bean上面加
     */
//    @ConditionalOnProperty(prefix = "vote.webmvc", name = "enabled", havingValue = "true", matchIfMissing = true)
//    public static class WebMvcConfig {
//
//        @Bean
//        @ConditionalOnProperty(prefix = "vote.webmvc", name = "enabled", havingValue = "true", matchIfMissing = true)
//        public TenantWebMvcConfigurer getTenantWebMvcConfigurer() {
//            return new TenantWebMvcConfigurer();
//        }
//
//    }
}

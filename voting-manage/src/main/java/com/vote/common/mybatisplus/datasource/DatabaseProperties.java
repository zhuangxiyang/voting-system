package com.vote.common.mybatisplus.datasource;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 客户端认证配置
 *
 * @author vote
 */
@Component
@ConfigurationProperties(prefix = DatabaseProperties.PREFIX)
@Data
@NoArgsConstructor
public class DatabaseProperties {
    public static final String PREFIX = "vote.database";
    /**
     * 是否启用 防止全表更新与删除插件
     *
     * @return
     */
    public Boolean isBlockAttack = false;
    /**
     * 是否启用  sql性能规范插件
     */
    public Boolean isIllegalSql = false;
    /**
     * 是否启用 seata
     */
    public Boolean isSeata = false;
    /**
     * 分页大小限制
     */
    protected long limit = -1;

    protected DbType dbType = DbType.MYSQL;
    /**
     * 是否禁止写入
     */
    private Boolean isNotWrite = false;
    /**
     * 是否启用数据权限
     */
    private Boolean isDataScope = false;
    /**
     * 事务超时时间
     */
    private int txTimeout = 60 * 60;

    /**
     * 租户库 前缀
     */
    private String tenantDatabasePrefix = "vote_base";

    /**
     * 多租户模式
     */
    private MultiTenantType multiTenantType = MultiTenantType.SCHEMA;
    /**
     * 租户id 列名
     */
    private String tenantIdColumn = "tenant_code";


    /**
     * 统一管理事务的方法名
     */
    private List<String> transactionAttributeList = new ArrayList<>(Arrays.asList("add*", "save*", "insert*",
            "create*", "update*", "edit*", "upload*", "delete*", "remove*",
            "clean*", "recycle*", "batch*", "mark*", "disable*", "enable*", "handle*", "syn*",
            "reg*", "gen*", "*Tx"
    ));
    /**
     * 事务扫描基础包
     */
    private String transactionScanPackage = "com.vote";

}

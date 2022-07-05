package com.vote.server.dao;


import com.vote.common.mybatisplus.base.mapper.SuperMapper;
import com.vote.server.entity.SysSerialIndex;
import org.springframework.stereotype.Repository;

/**
 * 编号管理接口
 */
@Repository
public interface SerialIndexMapper extends SuperMapper<SysSerialIndex> {

    /**
     * 不存在则插入数据，存在则忽略本次操作
     * @param sysSerialIndex
     * @return
     */
    Integer insertIgnore(SysSerialIndex sysSerialIndex);
}

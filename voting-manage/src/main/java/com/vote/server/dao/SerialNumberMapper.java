package com.vote.server.dao;


import com.vote.common.mybatisplus.base.mapper.SuperMapper;
import com.vote.server.entity.SysSerialNumber;
import org.springframework.stereotype.Repository;

/**
 * 编号管理接口
 */
@Repository
public interface SerialNumberMapper extends SuperMapper<SysSerialNumber> {

}

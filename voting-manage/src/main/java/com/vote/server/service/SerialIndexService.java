package com.vote.server.service;


import com.vote.common.mybatisplus.base.service.SuperService;
import com.vote.server.dto.req.SerialIndexDto;
import com.vote.server.entity.SysSerialIndex;

import java.util.List;

/**
 * 当前编号统计
 */
public interface SerialIndexService extends SuperService<SysSerialIndex> {

    /**
     * 获取指定个数的流水号
     *
     * @param serialIndexDto
     * @param count
     * @return
     */
    List<Integer> getSerialIndex(SerialIndexDto serialIndexDto, Integer count);

}

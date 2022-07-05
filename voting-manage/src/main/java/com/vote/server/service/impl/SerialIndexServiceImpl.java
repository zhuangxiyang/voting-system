package com.vote.server.service.impl;


import com.vote.common.util.BaseUserUtil;
import com.vote.common.mybatisplus.base.constant.CoreConstant;
import com.vote.common.mybatisplus.base.service.SuperServiceImpl;
import com.vote.common.mybatisplus.mybatis.conditions.Wraps;
import com.vote.server.dao.SerialIndexMapper;
import com.vote.server.dto.req.SerialIndexDto;
import com.vote.server.entity.SysSerialIndex;
import com.vote.server.service.SerialIndexService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 角色业务接口实现
 * @author vote
 */
@Service
public class SerialIndexServiceImpl extends SuperServiceImpl<SerialIndexMapper, SysSerialIndex> implements SerialIndexService {

    @Override
    public List<Integer> getSerialIndex(SerialIndexDto serialIndexDto, Integer count) {
        Integer startIndex = null;
        while (true) {
            SysSerialIndex sysSerialIndex = initOrGet(serialIndexDto);
            boolean update = update(Wraps.<SysSerialIndex>lbU()
                    .set(SysSerialIndex::getSerialIndex, sysSerialIndex.getSerialIndex() + count)
                    .set(SysSerialIndex::getVersion, sysSerialIndex.getVersion() + 1)
                    .eq(SysSerialIndex::getSerialCode, sysSerialIndex.getSerialCode())
                    .eq(SysSerialIndex::getDelFlag, CoreConstant.FLAG_NORMAL)
                    .eq(SysSerialIndex::getVersion, sysSerialIndex.getVersion())
            );
            if (update) {
                startIndex = sysSerialIndex.getSerialIndex();
                break;
            }
        }
        List<Integer> resultList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            resultList.add(startIndex + i + 1);
        }
        return resultList;
    }

    public SysSerialIndex initOrGet(SerialIndexDto serialIndexDto) {
        SysSerialIndex codesIndex = get(serialIndexDto);
        if (Objects.nonNull(codesIndex)) {
            return codesIndex;
        }
        init(serialIndexDto);
        return get(serialIndexDto);
    }

    public void init(SerialIndexDto serialIndexDto) {
        String userCode = "admin";
        try{
            userCode = BaseUserUtil.getCurrentUser().getUserCode();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        SysSerialIndex sysSerialIndex = SysSerialIndex.builder()
                .serialNumCount(serialIndexDto.getSerialNumCount())
                .serialCode(serialIndexDto.getSerialCode())
                .serialDate(serialIndexDto.getSerialDate())
                .serialParam(serialIndexDto.getSerialParam())
                .serialIndex(0)
                .version(0)
                .createTime(new Date())
                .createUser(userCode)
                .updateUser(userCode)
                .updateTime(new Date())
                .delFlag(CoreConstant.FLAG_NORMAL)
                .build();
        baseMapper.insertIgnore(sysSerialIndex);
    }

    public SysSerialIndex get(SerialIndexDto serialIndexDto) {
        return getOne(Wraps.<SysSerialIndex>lbQ()
                .eq(SysSerialIndex::getSerialCode, serialIndexDto.getSerialCode())
                .eq(SysSerialIndex::getSerialNumCount, serialIndexDto.getSerialNumCount())
                .eq(SysSerialIndex::getSerialDate, serialIndexDto.getSerialDate())
                .eq(SysSerialIndex::getSerialParam, serialIndexDto.getSerialParam())
                .eq(SysSerialIndex::getDelFlag, CoreConstant.FLAG_NORMAL)
        );
    }
}

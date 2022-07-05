package com.vote.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperService;
import com.vote.server.dto.req.SerialNumberRequest;
import com.vote.server.dto.req.SerialNumberUpdateDTO;
import com.vote.server.entity.SysSerialNumber;

import java.util.List;
import java.util.Map;

/**
 * 编号管理
 */
public interface SerialNumberService extends SuperService<SysSerialNumber> {

    /**
     * 生成编码
     *
     * @param list
     * @return
     */
    public Map<String, List<String>> getSerialList(List<SerialNumberRequest> list);

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public IPage<SysSerialNumber> serialNumberList(PageParams<SysSerialNumber> pageParams);

    /**
     * 添加流水号
     *
     * @param sysSerialNumber
     */
    public void saveSerial(SysSerialNumber sysSerialNumber);

    /**
     * 修改流水号
     *
     * @param serialNumberUpdateDTO
     */
    void updateSerial(SerialNumberUpdateDTO serialNumberUpdateDTO);

    /**
     * 查询流水号详情
     *
     * @param serialCode
     * @return
     */
    SysSerialNumber querySerialInfo(String serialCode);

    /**
     * 批量删除流水号
     *
     * @param serialCodes
     */
    void batchDeleteSerial(List<String> serialCodes);
}

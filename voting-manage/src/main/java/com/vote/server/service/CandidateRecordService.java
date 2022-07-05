package com.vote.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperService;
import com.vote.server.dto.req.CandidateRecordReqDTO;
import com.vote.server.dto.resp.CandidateRecordRespDTO;
import com.vote.server.entity.CandidateRecord;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 候选人记录
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
public interface CandidateRecordService extends SuperService<CandidateRecord> {

    public IPage<CandidateRecordRespDTO> getCandidateRecordPage(PageParams<CandidateRecordReqDTO> pageParams);

    public Boolean saveCandidateRecord(CandidateRecord saveDTO);

    public Boolean deleteCandidateRecord(List<Long> idList);

    public List<CandidateRecordRespDTO> getCandidateRecords(String actNo);

    public List<CandidateRecordRespDTO> getCandidateRecordsByActNos(List<String> actNos);
}

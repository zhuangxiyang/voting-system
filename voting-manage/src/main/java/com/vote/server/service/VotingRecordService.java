package com.vote.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperService;
import com.vote.server.dto.req.VotingRecordReqDTO;
import com.vote.server.dto.resp.VotingRecordRespDTO;
import com.vote.server.entity.VotingRecord;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 投票记录
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
public interface VotingRecordService extends SuperService<VotingRecord> {

    public IPage<VotingRecordRespDTO> getVotingRecordPage(PageParams<VotingRecordReqDTO> pageParams);

    public Boolean saveVotingRecord(VotingRecord saveDTO);

    public Boolean deleteVotingRecord(List<Long> idList);

    public List<VotingRecordRespDTO> getVotingRecords(String actNos);

    public void check (String actNo,String voterIdCard);
}

package com.vote;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.server.controller.VotingRecordController;
import com.vote.server.dto.req.CandidateRecordReqDTO;
import com.vote.server.dto.req.VotingRecordReqDTO;
import com.vote.server.dto.resp.CandidateRecordRespDTO;
import com.vote.server.dto.resp.VotingRecordRespDTO;
import com.vote.server.entity.VotingRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class VotingRecordControllerTest {

    @Autowired
    private VotingRecordController votingRecordController;

    /**
     * 测试查看投票记录
     */
    @Test
    public void testGetVotingRecord(){
        PageParams<VotingRecordReqDTO> pageParams = new PageParams<>();
        VotingRecordReqDTO candidateRecord = new VotingRecordReqDTO();
        candidateRecord.setActNo("ACT220705012");
        pageParams.setModel(candidateRecord);
        Res<IPage<VotingRecordRespDTO>> votingRecordPage = votingRecordController.getVotingRecordPage(pageParams);
        System.out.println(JSON.toJSONString(votingRecordPage));
    }

    /**
     * 测试投票
     */
    @Test
    public void testSaveVotingRecord(){
        VotingRecord candidateRecord = new VotingRecord();
        candidateRecord.setActNo("ACT220705012");
        candidateRecord.setCandidateIdCard("A123445(6)");//候选人2:A123445(6) ; 候选人1:A123446(6)
        candidateRecord.setCandidateName("候选人2");//
        candidateRecord.setVoterName("vote-admin@cxd-logistics.com");
        candidateRecord.setVoterIdCard("A138336(5)");
        candidateRecord.setVoterMail("vote-admin@cxd-logistics.com");
        Res<IPage<CandidateRecordRespDTO>> candidateRecordPage = votingRecordController.saveVotingRecord(candidateRecord);
        System.out.println(JSON.toJSONString(candidateRecordPage));
    }
}

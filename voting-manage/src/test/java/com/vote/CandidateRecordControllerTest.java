package com.vote;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.server.controller.CandidateRecordController;
import com.vote.server.dto.req.ActivityReqDTO;
import com.vote.server.dto.req.CandidateRecordReqDTO;
import com.vote.server.dto.resp.CandidateRecordRespDTO;
import com.vote.server.entity.CandidateRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class CandidateRecordControllerTest {

    @Autowired
    private CandidateRecordController candidateRecordController;

    /**
     * 测试添加候选人
     */
    @Test
    public void testSaveCandidateRecord(){
        CandidateRecord candidateRecord = new CandidateRecord();
        candidateRecord.setActNo("ACT220705012");
        candidateRecord.setCandidateIdCard("A123446(6)");
        candidateRecord.setCandidateMail("vote-admin@cxd-logistics.com");
        candidateRecord.setCandidateName("候选人1");
        Res<Boolean> booleanRes = candidateRecordController.saveCandidateRecord(candidateRecord);
        System.out.println(JSON.toJSONString(booleanRes));
    }

    /**
     * 测试查看投票实时结果
     */
    @Test
    public void testGetCandidateRecord(){
        PageParams<CandidateRecordReqDTO> pageParams = new PageParams<>();
        CandidateRecordReqDTO candidateRecord = new CandidateRecordReqDTO();
        candidateRecord.setActNo("ACT220705012");
        pageParams.setModel(candidateRecord);
        Res<IPage<CandidateRecordRespDTO>> candidateRecordPage = candidateRecordController.getCandidateRecordPage(pageParams);
        System.out.println(JSON.toJSONString(candidateRecordPage));
    }
}

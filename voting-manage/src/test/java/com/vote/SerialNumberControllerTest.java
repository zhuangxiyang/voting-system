package com.vote;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.util.Constant;
import com.vote.server.controller.SerialNumberController;
import com.vote.server.dto.resp.CandidateRecordRespDTO;
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
public class SerialNumberControllerTest {

    @Autowired
    private SerialNumberController serialNumberController;

    /**
     * 测试获取投票编码
     */
    @Test
    public void testGetSingleSerial(){
        Res<String> singleSerial = serialNumberController.getSingleSerial(Constant.SerialCoses.SERIAL_VOTING_CODE);
        System.out.println(JSON.toJSONString(singleSerial));
    }
}

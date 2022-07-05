package com.vote;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.server.controller.VoteUserController;
import com.vote.server.dto.req.VoteUserReqDTO;
import com.vote.server.dto.resp.VoteUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class VoteUserControllerTest {

    @Autowired
    VoteUserController voteUserController;


    /**
     * 测试查看系统管理员
     */
    @Test
    public void testGetVoteUser(){
        PageParams<VoteUserReqDTO> pageParams = new PageParams<>();
        VoteUserReqDTO candidateRecord = new VoteUserReqDTO();
        pageParams.setModel(candidateRecord);
        Res<IPage<VoteUserRespDTO>> voteUserPage = voteUserController.getVoteUserPage(pageParams);
        System.out.println(JSON.toJSONString(voteUserPage));
    }
}

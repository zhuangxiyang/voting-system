package com.vote.server.controller;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.eventbus.EventBus;
import com.vote.common.dto.Res;
import com.vote.common.exception.BaseAssert;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.redis.util.RedisUtil;
import com.vote.common.util.Constant;
import com.vote.common.util.EmptyUtils;
import com.vote.common.util.IdCardUtil;
import com.vote.server.dto.req.VotingRecordReqDTO;
import com.vote.server.dto.resp.VotingRecordRespDTO;
import com.vote.server.entity.VotingRecord;
import com.vote.server.service.VotingRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * <p>
 * 前端控制器
 * 投票记录
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Slf4j
@RestController
@RequestMapping("/votingRecord")
@Api(value = "VotingRecord", tags = "投票记录")
public class VotingRecordController {

    @Resource
    EventBus eventBus;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VotingRecordService baseService;

    /**
     *  分页查询
     * @param pageParams
     * @return
     */
    @PostMapping("/getVotingRecordPage")
    @ApiOperation(value = "投票记录列表")
    public Res<IPage<VotingRecordRespDTO>> getVotingRecordPage(@RequestBody PageParams<VotingRecordReqDTO> pageParams){
        return Res.success(baseService.getVotingRecordPage(pageParams));
    }

    /**
     * 投票
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "投票")
    @PostMapping("/saveVotingRecord")
    public Res saveVotingRecord(@RequestBody @Validated VotingRecord saveDTO) {
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO) && EmptyUtils.isNotEmpty(saveDTO.getActNo()),"投票编码不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getCandidateIdCard()),"候选人身份证不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getCandidateName()),"候选人名称不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getVoterIdCard()),"投票人身份证不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getVoterMail()),"投票人邮箱不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getVoterName()),"投票人名称不能为空");
        BaseAssert.isTrue(IdCardUtil.isCorrect(saveDTO.getVoterIdCard()),"投票人身份证格式错误");

        //投票是否结束
        String actKey = Constant.ACT_REDIS_KEY+saveDTO.getActNo();
        BaseAssert.isTrue(redisUtil.exists(actKey),"投票已结束，不能参与投票");
        //投票状态校验:投票是否启用
        String actStatusKey = Constant.ACT_STATUS_REDIS_KEY+saveDTO.getActNo();
        BaseAssert.isTrue(redisUtil.exists(actStatusKey),"投票已暂停，不能参与投票");

        baseService.check(saveDTO.getActNo(),saveDTO.getVoterIdCard());
        eventBus.post(saveDTO);
        return Res.success();
    }

    /**
     * 删除
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "删除")
    @DeleteMapping("/deleteVotingRecord")
    public Res<Boolean> deleteVotingRecord(@RequestParam("idList") List<Long> idList) {
        return Res.success(baseService.deleteVotingRecord(idList));
    }
}

package com.vote.server.controller;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.server.dto.req.CandidateRecordReqDTO;
import com.vote.server.dto.resp.CandidateRecordRespDTO;
import com.vote.server.entity.CandidateRecord;
import com.vote.server.service.CandidateRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 候选人记录
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Slf4j
@RestController
@RequestMapping("/candidateRecord")
@Api(value = "CandidateRecord", tags = "候选人记录")
public class CandidateRecordController {

    @Autowired
    private CandidateRecordService baseService;

    /**
     *  分页查询
     * @param pageParams
     * @return
     */
    @PostMapping("/getCandidateRecordPage")
    @ApiOperation(value = "候选人记录列表")
    public Res<IPage<CandidateRecordRespDTO>> getCandidateRecordPage(@RequestBody PageParams<CandidateRecordReqDTO> pageParams){
        return Res.success(baseService.getCandidateRecordPage(pageParams));
    }

    /**
     * 新增或修改
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增或修改")
    @PostMapping("/saveCandidateRecord")
    public Res<Boolean> saveCandidateRecord(@RequestBody @Validated CandidateRecord saveDTO) {
        return Res.success(baseService.saveCandidateRecord(saveDTO));
    }

    /**
     * 删除
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "删除")
    @DeleteMapping("/deleteCandidateRecord")
    public Res<Boolean> deleteCandidateRecord(@RequestParam("idList") List<Long> idList) {
        return Res.success(baseService.deleteCandidateRecord(idList));
    }
}

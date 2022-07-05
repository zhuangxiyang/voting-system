package com.vote.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Entity;
import com.vote.common.exception.BaseAssert;
import com.vote.common.util.BaseUserUtil;
import com.vote.common.util.EmptyUtils;
import com.vote.common.mybatisplus.base.constant.CoreConstant;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperServiceImpl;
import com.vote.common.mybatisplus.mybatis.conditions.Wraps;
import com.vote.common.mybatisplus.utils.BeanPlusUtil;
import com.vote.common.util.IdCardUtil;
import com.vote.server.dao.CandidateRecordMapper;
import com.vote.server.dto.req.CandidateRecordReqDTO;
import com.vote.server.dto.resp.CandidateRecordRespDTO;
import com.vote.server.dto.resp.VotingRecordRespDTO;
import com.vote.server.entity.CandidateRecord;
import com.vote.server.service.CandidateRecordService;
import com.vote.server.service.VotingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 候选人记录
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Slf4j
@Service
public class CandidateRecordServiceImpl extends SuperServiceImpl<CandidateRecordMapper, CandidateRecord> implements CandidateRecordService {

    @Autowired
    private VotingRecordService votingRecordService;

    /**
     *  分页查询
     * @param pageParams
     * @return
     */
    @Override
    public IPage<CandidateRecordRespDTO> getCandidateRecordPage(PageParams<CandidateRecordReqDTO> pageParams){
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(pageParams.getModel()) && EmptyUtils.isNotEmpty(pageParams.getModel().getActNo()),"参数投票编码actNo不能为空");
        IPage<CandidateRecord> ipage = modelPage(pageParams);
        IPage<CandidateRecordRespDTO> pageDTO = pageParams.buildPage();
        BeanUtils.copyProperties(ipage, pageDTO);
        if (CollectionUtils.isEmpty(ipage.getRecords())) {
            return pageDTO;
        }
        List<CandidateRecordRespDTO> candidateRecordRespDTOS = BeanPlusUtil.toBeanList(ipage.getRecords(), CandidateRecordRespDTO.class);
        List<VotingRecordRespDTO> votingRecords = votingRecordService.getVotingRecords(pageParams.getModel().getActNo());
        if(EmptyUtils.isNotEmpty(votingRecords)){
            //根据候选人idcard分组
            Map<String, Long> idCardMap = votingRecords.stream().collect(Collectors.groupingBy(VotingRecordRespDTO::getCandidateIdCard,Collectors.counting()));
            candidateRecordRespDTOS.stream().forEach(v -> {
                if(EmptyUtils.isNotEmpty(idCardMap.get(v.getCandidateIdCard()))){
                    v.setNum(Math.toIntExact(idCardMap.get(v.getCandidateIdCard())));
                }
            });
        }
        //可对结果进行处理
        pageDTO.setRecords(candidateRecordRespDTOS);
        return pageDTO;
    }

    /**
     * 新增或修改
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @Override
    public Boolean saveCandidateRecord(CandidateRecord saveDTO) {
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO) && EmptyUtils.isNotEmpty(saveDTO.getActNo()),"投票编号不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getCandidateIdCard()),"候选人身份证不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getCandidateMail()),"候选人邮箱不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getCandidateName()),"候选人名称不能为空");
        BaseAssert.isTrue(IdCardUtil.isCorrect(saveDTO.getCandidateIdCard()),"候选人身份证格式错误");
        CandidateRecord model = BeanUtil.toBean(saveDTO, getEntityClass());
        boolean updateResult = false;
        if(EmptyUtils.isEmpty(saveDTO.getId())){
            int count = count(Wraps.<CandidateRecord>lbQ()
                    .eq(CandidateRecord::getActNo, saveDTO.getActNo())
                    .eq(CandidateRecord::getCandidateIdCard, saveDTO.getCandidateIdCard())
                    .eq(CandidateRecord::getDelFlag, CoreConstant.FLAG_NORMAL));
            BaseAssert.isTrue(count <= 0,"不能重复添加同一个候选人");
            //新增
            updateResult = save(model);
        }else{
            //修改
            model.setUpdateTime(new Date());
            model.setUpdateUser(BaseUserUtil.getCurrentUser().getUserCode());
            updateResult = update(model, Wraps.<CandidateRecord>u().eq(Entity.FIELD_ID, model.getId()));
        }
        BaseAssert.isTrue(updateResult,"新增或更新失败");
        return updateResult;
    }

    /**
     * 删除
     *
     * @param idList
     * @return
     */
    public Boolean deleteCandidateRecord(List<Long> idList){
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(idList),"传入参数[idList]为空");
        boolean deleteResult = update(Wraps.<CandidateRecord>u()
            .set(Entity.DEL_FLAG_COLUMN, CoreConstant.FLAG_DELETE)
            .set(Entity.UPDATE_TIME_COLUMN, new Date())
            .set(Entity.UPDATE_USER_COLUMN, BaseUserUtil.getCurrentUser().getUserCode())
            .in(Entity.FIELD_ID, idList));
        return deleteResult;
    }

    /**
     * 获取候选人
     * @param actNo
     * @return
     */
    @Override
    public List<CandidateRecordRespDTO> getCandidateRecords(String actNo){
        List<CandidateRecord> candidateRecords = list(Wraps.<CandidateRecord>lbQ()
                .eq(CandidateRecord::getActNo, actNo)
                .eq(CandidateRecord::getDelFlag, CoreConstant.FLAG_NORMAL));
        if(EmptyUtils.isEmpty(candidateRecords)){
            return new ArrayList<CandidateRecordRespDTO>();
        }
        return BeanPlusUtil.toBeanList(candidateRecords,CandidateRecordRespDTO.class);
    }

    /**
     * 获取候选人
     * @param actNos
     * @return
     */
    @Override
    public List<CandidateRecordRespDTO> getCandidateRecordsByActNos(List<String> actNos){
        List<CandidateRecord> candidateRecords = list(Wraps.<CandidateRecord>lbQ()
                .in(CandidateRecord::getActNo, actNos)
                .eq(CandidateRecord::getDelFlag, CoreConstant.FLAG_NORMAL));
        if(EmptyUtils.isEmpty(candidateRecords)){
            return new ArrayList<CandidateRecordRespDTO>();
        }
        return BeanPlusUtil.toBeanList(candidateRecords,CandidateRecordRespDTO.class);
    }
}

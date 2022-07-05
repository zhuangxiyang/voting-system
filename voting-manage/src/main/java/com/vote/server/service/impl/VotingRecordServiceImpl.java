package com.vote.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Entity;
import com.vote.common.exception.BaseAssert;
import com.vote.common.mybatisplus.base.constant.CoreConstant;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperServiceImpl;
import com.vote.common.mybatisplus.mybatis.conditions.Wraps;
import com.vote.common.mybatisplus.utils.BeanPlusUtil;
import com.vote.common.redis.util.RedisUtil;
import com.vote.common.util.BaseUserUtil;
import com.vote.common.util.Constant;
import com.vote.common.util.EmptyUtils;
import com.vote.server.dao.VotingRecordMapper;
import com.vote.server.dto.req.VotingRecordReqDTO;
import com.vote.server.dto.resp.VotingRecordRespDTO;
import com.vote.server.entity.VotingRecord;
import com.vote.server.service.VotingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 投票记录
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Slf4j
@Service
public class VotingRecordServiceImpl extends SuperServiceImpl<VotingRecordMapper, VotingRecord> implements VotingRecordService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     *  分页查询
     * @param pageParams
     * @return
     */
    @Override
    public IPage<VotingRecordRespDTO> getVotingRecordPage(PageParams<VotingRecordReqDTO> pageParams){
        IPage<VotingRecord> ipage = modelPage(pageParams);
        IPage<VotingRecordRespDTO> pageDTO = pageParams.buildPage();
        BeanUtils.copyProperties(ipage, pageDTO);
        if (CollectionUtils.isEmpty(ipage.getRecords())) {
            return pageDTO;
        }
        //可对结果进行处理
        pageDTO.setRecords(BeanPlusUtil.toBeanList(ipage.getRecords(), VotingRecordRespDTO.class));
        return pageDTO;
    }

    /**
     * 新增或修改
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @Override
    public Boolean saveVotingRecord(VotingRecord saveDTO) {
        //投票是否结束
        String actKey = Constant.ACT_REDIS_KEY+saveDTO.getActNo();
        BaseAssert.isTrue(redisUtil.exists(actKey),"投票已结束，不能参与投票");
        //投票状态校验:投票是否启用
        String actStatusKey = Constant.ACT_STATUS_REDIS_KEY+saveDTO.getActNo();
        BaseAssert.isTrue(redisUtil.exists(actStatusKey),"投票已暂停，不能参与投票");

        VotingRecord model = BeanUtil.toBean(saveDTO, getEntityClass());
        boolean updateResult = false;
        if(EmptyUtils.isEmpty(saveDTO.getId())){
            check(saveDTO.getActNo(),saveDTO.getVoterIdCard());
            //新增
            updateResult = saveWithOutToken(model);
        }else{
            //修改
            model.setUpdateTime(new Date());
            model.setUpdateUser(BaseUserUtil.getCurrentUser().getUserCode());
            updateResult = update(model, Wraps.<VotingRecord>u().eq(Entity.FIELD_ID, model.getId()));
        }
        BaseAssert.isTrue(updateResult,"新增或更新失败");
        return updateResult;
    }

    public void check (String actNo,String voterIdCard){
        int count = count(Wraps.<VotingRecord>lbQ()
                .eq(VotingRecord::getActNo, actNo)
                .eq(VotingRecord::getVoterIdCard, voterIdCard)
                .eq(VotingRecord::getDelFlag, CoreConstant.FLAG_NORMAL));
        BaseAssert.isTrue(count == 0,"每个用户在同一场投票中只能投票一次");
    }

    /**
     * 删除
     *
     * @param idList
     * @return
     */
    public Boolean deleteVotingRecord(List<Long> idList){
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(idList),"传入参数[idList]为空");
        boolean deleteResult = update(Wraps.<VotingRecord>u()
            .set(Entity.DEL_FLAG_COLUMN, CoreConstant.FLAG_DELETE)
            .set(Entity.UPDATE_TIME_COLUMN, new Date())
            .set(Entity.UPDATE_USER_COLUMN, BaseUserUtil.getCurrentUser().getUserCode())
            .in(Entity.FIELD_ID, idList));
        return deleteResult;
    }

    /**
     * 获取投票投票记录
     * @param actNo
     * @return
     */
    @Override
    public List<VotingRecordRespDTO> getVotingRecords(String actNo){
        List<VotingRecord> candidateRecords = list(Wraps.<VotingRecord>lbQ()
                .eq(VotingRecord::getActNo, actNo)
                .eq(VotingRecord::getDelFlag, CoreConstant.FLAG_NORMAL));
        if(EmptyUtils.isEmpty(candidateRecords)){
            return new ArrayList<VotingRecordRespDTO>();
        }
        return BeanPlusUtil.toBeanList(candidateRecords,VotingRecordRespDTO.class);
    }
}

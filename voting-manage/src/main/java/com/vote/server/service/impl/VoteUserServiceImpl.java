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
import com.vote.server.dao.VoteUserMapper;
import com.vote.server.dto.req.VoteUserReqDTO;
import com.vote.server.dto.resp.VoteUserRespDTO;
import com.vote.server.entity.VoteUser;
import com.vote.server.service.VoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 用户
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Slf4j
@Service
public class VoteUserServiceImpl extends SuperServiceImpl<VoteUserMapper, VoteUser> implements VoteUserService {

    /**
     *  分页查询
     * @param pageParams
     * @return
     */
    @Override
    public IPage<VoteUserRespDTO> getVoteUserPage(PageParams<VoteUserReqDTO> pageParams){
        IPage<VoteUser> ipage = modelPage(pageParams);
        IPage<VoteUserRespDTO> pageDTO = pageParams.buildPage();
        BeanUtils.copyProperties(ipage, pageDTO);
        if (CollectionUtils.isEmpty(ipage.getRecords())) {
            return pageDTO;
        }
        //可对结果进行处理
        pageDTO.setRecords(BeanPlusUtil.toBeanList(ipage.getRecords(), VoteUserRespDTO.class));
        return pageDTO;
    }

    /**
     * 新增或修改
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @Override
    public Boolean saveVoteUser(VoteUser saveDTO) {
        VoteUser model = BeanUtil.toBean(saveDTO, getEntityClass());
        boolean updateResult = false;
        if(EmptyUtils.isEmpty(saveDTO.getId())){
            //新增
            updateResult = save(model);
        }else{
            //修改
            model.setUpdateTime(new Date());
            model.setUpdateUser(BaseUserUtil.getCurrentUser().getUserCode());
            updateResult = update(model, Wraps.<VoteUser>u().eq(Entity.FIELD_ID, model.getId()));
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
    public Boolean deleteVoteUser(List<Long> idList){
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(idList),"传入参数[idList]为空");
        boolean deleteResult = update(Wraps.<VoteUser>u()
            .set(Entity.DEL_FLAG_COLUMN, CoreConstant.FLAG_DELETE)
            .set(Entity.UPDATE_TIME_COLUMN, new Date())
            .set(Entity.UPDATE_USER_COLUMN, BaseUserUtil.getCurrentUser().getUserCode())
            .in(Entity.FIELD_ID, idList));
        return deleteResult;
    }

    /**
     * 根据编码查询用户
     * @param userCode
     * @return
     */
    @Override
    public VoteUserRespDTO getUserByCode(String userCode){
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(userCode),"参数userCode不能为空");
        VoteUser one = getOne(Wraps.<VoteUser>lbQ()
                .eq(VoteUser::getUserCode, userCode)
                .eq(VoteUser::getDelFlag, CoreConstant.FLAG_NORMAL)
                .eq(VoteUser::getLoginStatus, CoreConstant.STATUS_ENABLE));
        if(EmptyUtils.isNotEmpty(one)){
            return BeanUtil.toBean(one,VoteUserRespDTO.class);
        }else{
            return new VoteUserRespDTO();
        }


    }
}

package com.vote.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperService;
import com.vote.server.dto.req.VoteUserReqDTO;
import com.vote.server.dto.resp.VoteUserRespDTO;
import com.vote.server.entity.VoteUser;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 用户
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
public interface VoteUserService extends SuperService<VoteUser> {

    public IPage<VoteUserRespDTO> getVoteUserPage(PageParams<VoteUserReqDTO> pageParams);

    public Boolean saveVoteUser(VoteUser saveDTO);

    public Boolean deleteVoteUser(List<Long> idList);

    public VoteUserRespDTO getUserByCode(String userCode);
}

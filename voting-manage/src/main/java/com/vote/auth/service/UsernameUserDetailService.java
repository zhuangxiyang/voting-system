package com.vote.auth.service;

import com.vote.common.exception.AuthException;
import com.vote.common.util.EmptyUtils;
import com.vote.server.dto.resp.VoteUserRespDTO;
import org.springframework.stereotype.Service;

@Service
public class UsernameUserDetailService extends BaseUserDetailService {

    @Override
    protected VoteUserRespDTO getUser(String userCode) {
        VoteUserRespDTO user = userService.getUserByCode(userCode);
        if(user==null || EmptyUtils.isEmpty(user.getUserCode())){
            throw new AuthException("用户不存在");
        }
        return user;
    }
}

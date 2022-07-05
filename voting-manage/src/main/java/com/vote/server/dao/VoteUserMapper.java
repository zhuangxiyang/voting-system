package com.vote.server.dao;

import com.vote.common.mybatisplus.base.mapper.SuperMapper;
import com.vote.server.entity.VoteUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 用户
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Repository
public interface VoteUserMapper extends SuperMapper<VoteUser> {

}

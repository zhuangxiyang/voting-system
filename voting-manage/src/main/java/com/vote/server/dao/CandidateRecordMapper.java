package com.vote.server.dao;

import com.vote.common.mybatisplus.base.mapper.SuperMapper;
import com.vote.server.entity.CandidateRecord;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 候选人记录
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Repository
public interface CandidateRecordMapper extends SuperMapper<CandidateRecord> {

}

package com.vote;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.eventbus.Subscribe;
import com.vote.bean.VotingRecordTestBean;
import com.vote.server.entity.VotingRecord;
import com.vote.server.service.VotingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VotingTestCallable {

    @Autowired
    private VotingRecordService votingRecordService;

    @Subscribe
    public void call(VotingRecordTestBean data) {
        try {
            votingRecordService.saveVotingRecord(BeanUtil.toBean(data, VotingRecord.class));
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }
    }
}

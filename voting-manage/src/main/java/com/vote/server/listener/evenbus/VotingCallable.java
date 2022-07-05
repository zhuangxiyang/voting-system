package com.vote.server.listener.evenbus;

import com.google.common.eventbus.Subscribe;
import com.vote.server.entity.VotingRecord;
import com.vote.server.service.VotingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VotingCallable {

    @Autowired
    private VotingRecordService votingRecordService;

    @Subscribe
    public void call(VotingRecord data) {
        try {
            votingRecordService.saveVotingRecord(data);
        } catch (RuntimeException exception) {

        }
    }
}

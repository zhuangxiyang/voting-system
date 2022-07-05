package com.vote;

import com.google.common.eventbus.EventBus;
import com.vote.bean.VotingRecordTestBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


 @RunWith(SpringRunner.class)
 @Slf4j
 @SpringBootTest
 public class EventBusTest {

     @Resource
     EventBus eventBus;

     @Test
     public void votingHandle(){
         VotingRecordTestBean voting = new VotingRecordTestBean();
         voting.setActNo("111");
         voting.setCandidateIdCard("231231");
         voting.setCandidateName("23424");
         voting.setVoterIdCard("2121");
         voting.setVoterMail("sfdaf");
         voting.setVoterName("3424");
         eventBus.post(voting);
     }


 }

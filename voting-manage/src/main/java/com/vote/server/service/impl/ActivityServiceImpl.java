package com.vote.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Entity;
import com.vote.common.exception.BaseAssert;
import com.vote.common.mail.MailService;
import com.vote.common.mybatisplus.base.constant.CoreConstant;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperServiceImpl;
import com.vote.common.mybatisplus.mybatis.conditions.Wraps;
import com.vote.common.mybatisplus.utils.BeanPlusUtil;
import com.vote.common.redis.util.RedisUtil;
import com.vote.common.threadpool.ThreadPoolService;
import com.vote.common.util.BaseUserUtil;
import com.vote.common.util.Constant;
import com.vote.common.util.EmptyUtils;
import com.vote.server.dao.ActivityMapper;
import com.vote.server.dto.ActCandidatorNumDTO;
import com.vote.server.dto.req.ActivityReqDTO;
import com.vote.server.dto.req.SerialNumberRequest;
import com.vote.server.dto.resp.ActivityRespDTO;
import com.vote.server.dto.resp.CandidateRecordRespDTO;
import com.vote.server.dto.resp.VotingRecordRespDTO;
import com.vote.server.entity.Activity;
import com.vote.server.service.ActivityService;
import com.vote.server.service.CandidateRecordService;
import com.vote.server.service.SerialNumberService;
import com.vote.server.service.VotingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 投票
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Slf4j
@Service
public class ActivityServiceImpl extends SuperServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MailService mailService;
    @Autowired
    private VotingRecordService votingRecordService;
    @Autowired
    private SerialNumberService serialNumberService;
    @Autowired
    private CandidateRecordService candidateRecordService;

    /**
     *  分页查询
     * @param pageParams
     * @return
     */
    @Override
    public IPage<ActivityRespDTO> getActivityPage(PageParams<ActivityReqDTO> pageParams){
        IPage<Activity> ipage = modelPage(pageParams);
        IPage<ActivityRespDTO> pageDTO = pageParams.buildPage();
        BeanUtils.copyProperties(ipage, pageDTO);
        if (CollectionUtils.isEmpty(ipage.getRecords())) {
            return pageDTO;
        }
        //可对结果进行处理
        pageDTO.setRecords(BeanPlusUtil.toBeanList(ipage.getRecords(), ActivityRespDTO.class));
        return pageDTO;
    }

    /**
     * 新增或修改
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @Override
    public Boolean saveActivity(Activity saveDTO) {
        Activity model = BeanUtil.toBean(saveDTO, getEntityClass());
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getActEndTime()),"投票结束时间不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getActName()),"投票名称不能为空");
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(saveDTO.getActStartTime()),"投票开始时间不能为空");
        BaseAssert.isTrue(saveDTO.getActStartTime().before(saveDTO.getActEndTime()),"投票开始时间必须小于投票结束时间");
        BaseAssert.isTrue(saveDTO.getActStartTime().after(new Date()),"投票开始时间必须大于当前时间");
        BaseAssert.isTrue(saveDTO.getActEndTime().after(new Date()),"投票结束时间必须大于当前时间");
        boolean updateResult = false;
        String actNo = saveDTO.getActNo();
        if(EmptyUtils.isEmpty(saveDTO.getId())){
            //新增
            model.setActStatus(Constant.DictionaryTypes.VOTING_STATUS_NEW);
            SerialNumberRequest serialNumberRequest = new SerialNumberRequest();
            serialNumberRequest.setSerialCode(Constant.SerialCoses.SERIAL_VOTING_CODE);
            List<SerialNumberRequest> list = new ArrayList<>();
            list.add(serialNumberRequest);
            actNo = serialNumberService.getSerialList(list).get(Constant.SerialCoses.SERIAL_VOTING_CODE).get(0);
            model.setActNo(actNo);
            updateResult = save(model);
        }else{
            BaseAssert.isTrue(!saveDTO.getActStatus().equals(Constant.DictionaryTypes.VOTING_STATUS_OVER),"投票已结束，不能修改");
            //修改
            model.setUpdateTime(new Date());
            model.setUpdateUser(BaseUserUtil.getCurrentUser().getUserCode());
            updateResult = update(model, Wraps.<Activity>u().eq(Entity.FIELD_ID, model.getId()));
        }
        //增加redis缓存，设置投票到期失效
        String redisKey = Constant.ACT_REDIS_KEY + actNo;
        redisUtil.set(redisKey,actNo);
        redisUtil.expireAt(redisKey, saveDTO.getActEndTime());
        BaseAssert.isTrue(updateResult,"新增或更新失败");
        return updateResult;
    }

    /**
     * 删除
     *
     * @param idList
     * @return
     */
    public Boolean deleteActivity(List<Long> idList){
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(idList),"传入参数[idList]为空");

        List<Activity> list = list(Wraps.<Activity>lbQ().in(Activity::getId).eq(Activity::getDelFlag,CoreConstant.FLAG_NORMAL ));
        BaseAssert.isTrue(idList.size() == list.size(),"投票不存在");

        boolean deleteResult = update(Wraps.<Activity>u()
            .set(Entity.DEL_FLAG_COLUMN, CoreConstant.FLAG_DELETE)
            .set(Entity.UPDATE_TIME_COLUMN, new Date())
            .set(Entity.UPDATE_USER_COLUMN, BaseUserUtil.getCurrentUser().getUserCode())
            .in(Entity.FIELD_ID, idList));
        return deleteResult;
    }

    private List<Activity> getActivities(List<String> actNos){
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(actNos),"参数actNos不能为空");
        //1、查询投票
        List<Activity> activities = list(Wraps.<Activity>lbQ()
                .eq(Activity::getDelFlag, CoreConstant.FLAG_NORMAL)
                .in(Activity::getActNo, actNos));
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(activities),"未找到对应的投票");
        return activities;
    }

    /**
     * 结束投票
     * @param actNos
     * @return
     */
    @Override
    public String activityOver(List<String> actNos){
        StringBuffer returnMsg = new StringBuffer();
        //1、查询投票
        List<Activity> activities = getActivities(actNos);
        //2、按投票统计票数
        //通知投票人结果列表
        List<VotingRecordRespDTO> preMailVotors = new ArrayList<>();
        Map<String,String> actResult = new HashMap<>();
        List<String> collect = activities.stream().filter(v -> v.getActStatus().equals(Constant.DictionaryTypes.VOTING_STATUS_OVER)).map(Activity::getActNo).collect(Collectors.toList());
        BaseAssert.isTrue(EmptyUtils.isEmpty(collect),"投票："+collect.stream().collect(Collectors.joining(","))+"已结束");
        activities.stream().forEach(v -> {
            //删除投票rediskey，后续的投票请求不再保存
            String redisKey = Constant.ACT_REDIS_KEY+v.getActNo();
            redisUtil.del(redisKey);
            //查询投票对应投票记录
            List<VotingRecordRespDTO> votingRecords = votingRecordService.getVotingRecords(v.getActNo());
            v.setActStatus(Constant.DictionaryTypes.VOTING_STATUS_OVER);
            v.setUpdateTime(new Date());
            v.setUpdateUser("admin");
            //统计票数
            if(EmptyUtils.isNotEmpty(votingRecords)){
                List<CandidateRecordRespDTO> candidateRecords = candidateRecordService.getCandidateRecords(v.getActNo());
                Map<String, List<CandidateRecordRespDTO>> candidateMap = candidateRecords.stream().collect(Collectors.groupingBy(CandidateRecordRespDTO::getCandidateIdCard));
                //根据候选人idcard分组
                Map<String, List<VotingRecordRespDTO>> idCardMap = votingRecords.stream().collect(Collectors.groupingBy(VotingRecordRespDTO::getCandidateIdCard));
                Map<String, Long> votingNumMap = votingRecords.stream().collect(Collectors.groupingBy(VotingRecordRespDTO::getCandidateIdCard, Collectors.counting()));

                //获取候选人对应票数集合
                List<ActCandidatorNumDTO> actCandidatorNumList = new ArrayList<>();
                StringBuilder voteMessage = new StringBuilder();
                AtomicInteger i = new AtomicInteger();
                votingNumMap.forEach((key,value) -> {
                    voteMessage.append("候选人" + i.get() + ":");
                    voteMessage.append(idCardMap.get(key).get(0).getCandidateName());
                    voteMessage.append(",票数:");
                    voteMessage.append(value);
                    voteMessage.append("; <br/>");
                    ActCandidatorNumDTO actCandidatorNumDTO = new ActCandidatorNumDTO();
                    actCandidatorNumDTO.setCandidateIdCard(key);
                    actCandidatorNumDTO.setNum(Math.toIntExact(value));
                    actCandidatorNumList.add(actCandidatorNumDTO);
                    i.getAndIncrement();
                });
                actResult.put(v.getActNo(),voteMessage.toString());
                //获取最大票数候选人
                Optional<ActCandidatorNumDTO> max = actCandidatorNumList.stream().max(Comparator.comparingInt(ActCandidatorNumDTO::getNum));
                v.setWinner(idCardMap.get(max.get().getCandidateIdCard()).get(0).getCandidateName());
                v.setWinnerIdCard(max.get().getCandidateIdCard());
                v.setWinnerNum(max.get().getNum().toString());

                //收集待发邮件用户
                idCardMap.forEach((key,value) -> {
                    value.stream().forEach(votingRecord -> {
                        votingRecord.setNum(Math.toIntExact(votingNumMap.get(votingRecord.getCandidateIdCard())));
                        if(max.get().getCandidateIdCard().equals(votingRecord.getCandidateIdCard())){
                            votingRecord.setIsWin(true);
                        }else{
                            votingRecord.setIsWin(false);
                        }
                        votingRecord.setCandidateMail(candidateMap.get(max.get().getCandidateIdCard()).get(0).getCandidateMail());
                        preMailVotors.add(votingRecord);
                    });
                });
                returnMsg.append("投票："+ v.getActNo() + "的总投票人数：" + candidateRecords.size());
                returnMsg.append("候选人的投票结果：" + voteMessage.toString() + ";");
            }
        });

        //3、更新投票状态，获选人，获选人票数
        saveOrUpdateBatch(activities);
        //4、异步发送邮件
        if(EmptyUtils.isNotEmpty(preMailVotors)){

            ThreadPoolService.getInstance().execute(() -> {
                Map<String, List<Activity>> actMap = activities.stream().collect(Collectors.groupingBy(Activity::getActNo));
                //给候选人发送邮件
                Map<String, List<VotingRecordRespDTO>> candidateMap = preMailVotors.stream().collect(Collectors.groupingBy(VotingRecordRespDTO::getCandidateIdCard));
                candidateMap.forEach((key,value) -> {
                    String actName = actMap.get(value.get(0).getActNo()).get(0).getActName();
                    String title = "关于【" + actName + "】的投票结果公布";
                    String message = "";
                    if(value.get(0).getIsWin()){
                        message = "恭喜你获得本次【" + actName + "】的胜利！<br/>";//如果候选人获胜，则会恭喜对方
                    }

                    message = message + actResult.get(value.get(0).getActNo());
                    mailService.sendMail(value.get(0).getCandidateMail(),getMailMessage(value.get(0).getCandidateName(),title,message),title,"VOTE-SYSTEM");
                });
                //给投票人发送邮件
                preMailVotors.stream().forEach(v -> {
                    String actName = actMap.get(v.getActNo()).get(0).getActName();
                    String title = "关于【" + actName + "】的投票结果公布";
                    mailService.sendMail(v.getVoterMail(),getMailMessage(v.getVoterName(),title,actResult.get(v.getActNo())),title,"VOTE-SYSTEM");
                });
            });
        }

        return returnMsg.toString();
    }

    /**
     * 组装邮件内容
     * @param userName
     * @param title
     * @param message
     * @return
     */
    private String getMailMessage(String userName,String title,String message){
        StringBuffer mailMessage = new StringBuffer();
        mailMessage.append("<br/><br/><div style =\"margin:0 auto;width:45%\">");
        mailMessage.append("<div style=\"text-align:center;font-weight: bold;font-size: 25px;\">");
        mailMessage.append(title);
        mailMessage.append("<br/><br/></div>");
        mailMessage.append("<div style=\"font-size: 12px;\">");
        mailMessage.append(userName);
        mailMessage.append("，您好,</div><br/>");
        mailMessage.append("<div style=\"font-size: 12px; word-wrap:break-word;word-break: break-all;\">");
        mailMessage.append(message);
        mailMessage.append(".感谢您的参与，谢谢！</div><br/>");
        mailMessage.append("</div>");
        return message.toString();
    }

    /**
     * 启用投票
     * @param actNos
     * @return
     */
    public Boolean enableActivity(List<String> actNos){
        //1、查询投票
        List<Activity> activities = getActivities(actNos);
        List<CandidateRecordRespDTO> candidateRecordsByActNos = candidateRecordService.getCandidateRecordsByActNos(actNos);
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(candidateRecordsByActNos),"投票：" + actNos.stream().collect(Collectors.joining(",")) + "未添加候选人");
        Map<String, Long> candidatorNum = candidateRecordsByActNos.stream().collect(Collectors.groupingBy(CandidateRecordRespDTO::getActNo, Collectors.counting()));
        StringBuilder errorMsg = new StringBuilder();
        candidatorNum.forEach((actNo,num) -> {
            if(num < 2){
                errorMsg.append(actNo);
            }
        });
        BaseAssert.isTrue(EmptyUtils.isEmpty(errorMsg.toString()),"投票："+errorMsg.toString()+"不满2位候选人,不允许启用;");
        activities.stream().forEach(v -> {
            //增加redis状态，只有拥有该状态才可以开始投票
            String redisKey = Constant.ACT_STATUS_REDIS_KEY+v.getActNo();
            redisUtil.set(redisKey,v.getActNo()+v.getActStartTime());
            v.setActStatus(Constant.DictionaryTypes.VOTING_STATUS_RUNNING);
            v.setUpdateTime(new Date());
            v.setUpdateUser("admin");
        });
        //更新投票状态
        saveOrUpdateBatch(activities);
        return true;
    }

    /**
     * 停用投票
     * @param actNos
     * @return
     */
    public Boolean stopActivity(List<String> actNos){
        //1、查询投票
        List<Activity> activities = getActivities(actNos);
        activities.stream().forEach(v -> {
            //删除redis状态，停止投票
            String redisKey = Constant.ACT_STATUS_REDIS_KEY+v.getActNo();
            redisUtil.del(redisKey);
            v.setActStatus(Constant.DictionaryTypes.VOTING_STATUS_STOP);
            v.setUpdateTime(new Date());
            v.setUpdateUser("admin");
        });
        //更新投票状态
        saveOrUpdateBatch(activities);
        return true;
    }
}

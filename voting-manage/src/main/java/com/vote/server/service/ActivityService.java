package com.vote.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperService;
import com.vote.server.dto.req.ActivityReqDTO;
import com.vote.server.dto.resp.ActivityRespDTO;
import com.vote.server.entity.Activity;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 投票
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
public interface ActivityService extends SuperService<Activity> {

    public IPage<ActivityRespDTO> getActivityPage(PageParams<ActivityReqDTO> pageParams);

    public Boolean saveActivity(Activity saveDTO);

    public Boolean deleteActivity(List<Long> idList);

    public String activityOver(List<String> actNos);

    public Boolean enableActivity(List<String> actNos);

    public Boolean stopActivity(List<String> actNos);

}

package com.vote;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.util.DateUtils;
import com.vote.server.controller.ActivityController;
import com.vote.server.dto.req.ActivityReqDTO;
import com.vote.server.dto.resp.ActivityRespDTO;
import com.vote.server.entity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class ActivityControllerTest {

    @Resource
    ActivityController activityController;

    /**
     * 查询投票接口测试
     */
    @Test
    public void testGetActivities(){
        PageParams<ActivityReqDTO> pageParams = new PageParams<>();
        ActivityReqDTO activityReqDTO = new ActivityReqDTO();
        activityReqDTO.setActNo("ACT220705012");
        pageParams.setModel(activityReqDTO);
        Res<IPage<ActivityRespDTO>> activityPage = activityController.getActivityPage(pageParams);
        System.out.println(JSON.toJSONString(activityPage));
    }

    /**
     * 新增投票接口测试
     */
    @Test
    public void testAddActivities(){
        Activity activity = new Activity();
        activity.setActName("投票测试");
        activity.setActStartTime(DateUtils.parse("2022-07-05 22:50:00"));
        activity.setActEndTime(DateUtils.parse("2022-07-06 12:00:23"));
        activity.setCreateUser("admin");
        activity.setUpdateUser("admin");
        Res<Boolean> booleanRes = activityController.saveActivity(activity);
        System.out.println(JSON.toJSONString(booleanRes));
    }

    /**
     * 删除投票接口测试
     */
    @Test
    public void testDeleteActivities(){
        Long[] idList = new Long[]{1l};
        Res<Boolean> booleanRes = activityController.deleteActivity(Arrays.asList(idList));
        System.out.println(JSON.toJSONString(booleanRes));
    }

    /**
     * 启用投票接口测试
     */
    @Test
    public void testEnableActivities(){
        String[] actNos = new String[]{"ACT220705012"};
        Res<Boolean> booleanRes = activityController.enableActivity(Arrays.asList(actNos));
        System.out.println(JSON.toJSONString(booleanRes));
    }

    /**
     * 停用投票接口测试
     */
    @Test
    public void testStopDeleteActivities(){
        String[] actNos = new String[]{"ACT220705012"};
        Res<Boolean> booleanRes = activityController.stopActivity(Arrays.asList(actNos));
        System.out.println(JSON.toJSONString(booleanRes));
    }

    /**
     * 结束投票接口测试
     */
    @Test
    public void testOverActivities(){
        String[] actNos = new String[]{"ACT220705012"};
        Res<String> booleanRes = activityController.activityOver(Arrays.asList(actNos));
        System.out.println(JSON.toJSONString(booleanRes));
    }
}

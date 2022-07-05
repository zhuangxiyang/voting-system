package com.vote.server.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.server.dto.req.ActivityReqDTO;
import com.vote.server.dto.resp.ActivityRespDTO;
import com.vote.server.entity.Activity;
import com.vote.server.service.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 投票
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Slf4j
@RestController
@RequestMapping("/activity")
@Api(value = "Activity", tags = "投票")
public class ActivityController {

    @Autowired
    private ActivityService baseService;

    /**
     *  分页查询
     * @param pageParams
     * @return
     */
    @PostMapping("/getActivityPage")
    @ApiOperation(value = "投票列表")
    public Res<IPage<ActivityRespDTO>> getActivityPage(@RequestBody PageParams<ActivityReqDTO> pageParams){
        return Res.success(baseService.getActivityPage(pageParams));
    }

    /**
     * 新增或修改
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增或修改")
    @PostMapping("/saveActivity")
    public Res<Boolean> saveActivity(@RequestBody @Validated Activity saveDTO) {
        return Res.success(baseService.saveActivity(saveDTO));
    }

    /**
     * 删除
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "删除")
    @DeleteMapping("/deleteActivity")
    public Res<Boolean> deleteActivity(@RequestParam("idList") List<Long> idList) {
        return Res.success(baseService.deleteActivity(idList));
    }

    /**
     * 结束投票
     * @param actNos
     * @return
     */
    @ApiOperation(value = "结束投票")
    @PutMapping("/activityOver")
    public Res<String> activityOver(@RequestParam("actNos") List<String> actNos) {
        return Res.success(baseService.activityOver(actNos));
    }

    /**
     * 启用投票
     * @param actNos
     * @return
     */
    @ApiOperation(value = "启用投票")
    @PutMapping("/enableActivity")
    public Res<Boolean> enableActivity(@RequestParam("actNos") List<String> actNos) {
        return Res.success(baseService.enableActivity(actNos));
    }

    /**
     * 停用投票
     * @param actNos
     * @return
     */
    @ApiOperation(value = "停用投票")
    @PutMapping("/stopActivity")
    public Res<Boolean> stopActivity(@RequestParam("actNos") List<String> actNos) {
        return Res.success(baseService.stopActivity(actNos));
    }
}

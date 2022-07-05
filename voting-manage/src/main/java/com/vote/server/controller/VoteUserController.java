package com.vote.server.controller;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.server.dto.req.VoteUserReqDTO;
import com.vote.server.dto.resp.VoteUserRespDTO;
import com.vote.server.entity.VoteUser;
import com.vote.server.service.VoteUserService;
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
 * 用户
 * </p>
 *
 * @author vote
 * @date 2022-07-02
 */
@Slf4j
@RestController
@RequestMapping("/voteUser")
@Api(value = "VoteUser", tags = "用户")
public class VoteUserController {

    @Autowired
    private VoteUserService baseService;

    /**
     *  分页查询
     * @param pageParams
     * @return
     */
    @PostMapping("/getVoteUserPage")
    @ApiOperation(value = "用户列表")
    public Res<IPage<VoteUserRespDTO>> getVoteUserPage(@RequestBody PageParams<VoteUserReqDTO> pageParams){
        return Res.success(baseService.getVoteUserPage(pageParams));
    }

    /**
     * 新增或修改
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增或修改")
    @PostMapping("/saveVoteUser")
    public Res<Boolean> saveVoteUser(@RequestBody @Validated VoteUser saveDTO) {
        return Res.success(baseService.saveVoteUser(saveDTO));
    }

    /**
     * 删除
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "删除")
    @DeleteMapping("/deleteVoteUser")
    public Res<Boolean> deleteVoteUser(@RequestParam("idList") List<Long> idList) {
        return Res.success(baseService.deleteVoteUser(idList));
    }
}

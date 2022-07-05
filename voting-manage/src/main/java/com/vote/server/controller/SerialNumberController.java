package com.vote.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Res;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.server.dto.req.SerialNumberRequest;
import com.vote.server.dto.req.SerialNumberUpdateDTO;
import com.vote.server.entity.SysSerialNumber;
import com.vote.server.service.SerialNumberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "流水号管理")
@RequestMapping("/serial")
public class SerialNumberController {

    @Resource
    private SerialNumberService serialNumberService;

    /**
     * 批量获取流水号
     * @param list
     * @return
     */
    @ApiOperation(value = "批量获取流水号", notes = "批量获取流水号")
    @PostMapping("/getSerialList")
    public Res<Map<String, List<String>>> getSerialList(@RequestBody List<SerialNumberRequest> list) {
        try{
            return Res.success(serialNumberService.getSerialList(list));
        }catch (Exception e){
            return new Res(-1,new HashMap<String, List<String>>(),e.getMessage());
        }
    }

    /**
     * 根据code，数量获取流水号
     * @param serialNumberRequest
     * @return
     */
    @ApiOperation(value = "根据code，数量获取流水号", notes = "根据code，数量获取流水号")
    @PostMapping("/getSerial")
    public Res<List<String>> getSerial(@RequestBody SerialNumberRequest serialNumberRequest) {
        List<SerialNumberRequest> list = new ArrayList<>();
        list.add(serialNumberRequest);
        return Res.success(serialNumberService.getSerialList(list).get(serialNumberRequest.getSerialCode()));
    }

    /**
     * 根据code获取流水号
     * @param serialCode
     * @return
     */
    @ApiOperation(value = "根据code获取流水号", notes = "根据code获取流水号")
    @GetMapping("/getSingleSerial")
    public Res<String> getSingleSerial(@RequestParam(value = "serialCode") String serialCode) {
        SerialNumberRequest serialNumberRequest = new SerialNumberRequest();
        serialNumberRequest.setSerialCode(serialCode);
        List<SerialNumberRequest> list = new ArrayList<>();
        list.add(serialNumberRequest);
        return Res.success(serialNumberService.getSerialList(list).get(serialCode).get(0));
    }

    /**
     * 添加流水号
     * @param sysSerialNumber
     * @return
     */
    @PostMapping("/saveSerialNumber")
    @ApiOperation(value = "添加流水号")
    public Res saveUser(@RequestBody SysSerialNumber sysSerialNumber){
        serialNumberService.saveSerial(sysSerialNumber);
        return Res.success();
    }

    /**
     * 修改流水号
     * @param serialNumberUpdateDTO
     * @return
     */
    @PostMapping("/updateSerialNumber")
    @ApiOperation(value = "修改流水号")
    public Res updateSerialNumber(@RequestBody SerialNumberUpdateDTO serialNumberUpdateDTO){
        serialNumberService.updateSerial(serialNumberUpdateDTO);
        return Res.success();
    }

    /**
     * 查询流水号列表
     * @param pageParams
     * @return
     */
    @PostMapping("/list")
    @ApiOperation(value = "流水号列表")
    public Res<IPage<SysSerialNumber>> userList(@RequestBody PageParams<SysSerialNumber> pageParams){
        return Res.success(serialNumberService.serialNumberList(pageParams));
    }

    /**
     * 查询流水号详情
     * @param serialCode
     * @return
     */
    @GetMapping("/querySerialInfo")
    @ApiOperation(value = "querySerialInfo")
    public Res<SysSerialNumber> querySerialInfo(@RequestParam("serialCode") String serialCode){
        return Res.success(serialNumberService.querySerialInfo(serialCode));
    }

    /**
     * 批量删除流水号
     * @param serialCodes
     * @return
     */
    @GetMapping("/batchDeleteSerial")
    @ApiOperation(value = "batchDeleteSerial")
    public Res batchDeleteSerial(@RequestParam("serialCodes") List<String> serialCodes){
        serialNumberService.batchDeleteSerial(serialCodes);
        return Res.success();
    }

}

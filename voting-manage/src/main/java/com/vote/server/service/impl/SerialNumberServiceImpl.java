package com.vote.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vote.common.dto.Entity;
import com.vote.common.exception.BaseAssert;
import com.vote.common.util.BaseUserUtil;
import com.vote.common.util.EmptyUtils;
import com.vote.common.mybatisplus.base.constant.CoreConstant;
import com.vote.common.mybatisplus.base.request.PageParams;
import com.vote.common.mybatisplus.base.service.SuperServiceImpl;
import com.vote.common.mybatisplus.mybatis.conditions.Wraps;
import com.vote.common.mybatisplus.mybatis.conditions.query.QueryWrap;
import com.vote.server.dao.SerialNumberMapper;
import com.vote.server.dto.req.SerialIndexDto;
import com.vote.server.dto.req.SerialNumberRequest;
import com.vote.server.dto.req.SerialNumberUpdateDTO;
import com.vote.server.entity.SysSerialNumber;
import com.vote.server.service.SerialIndexService;
import com.vote.server.service.SerialNumberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 角色业务接口实现
 * @author vote
 */
@Service
@Slf4j
public class SerialNumberServiceImpl extends SuperServiceImpl<SerialNumberMapper, SysSerialNumber> implements SerialNumberService {

    @Autowired
    private SerialIndexService serialIndexService;

    /**
     * 分页查询
     * @param pageParams
     * @return
     */
    @Override
    public IPage<SysSerialNumber> serialNumberList(PageParams<SysSerialNumber> pageParams) {
        IPage<SysSerialNumber> ipage = pageParams.buildPage();
        SysSerialNumber model = BeanUtil.toBean(pageParams.getModel(), getEntityClass());
        QueryWrap<SysSerialNumber> wrapper = Wraps.q(model, pageParams.getMap(), getEntityClass());
        //只查未删除的数据
        wrapper.eq(Entity.DEL_FLAG_COLUMN, CoreConstant.FLAG_NORMAL);
        page(ipage, wrapper);
        return ipage;
    }

    /**
     * 添加流水号
     * @param sysSerialNumber
     */
    @Override
    public void saveSerial(SysSerialNumber sysSerialNumber) {
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(sysSerialNumber) && EmptyUtils.isNotEmpty(sysSerialNumber.getSerialCode()),"待保存数据为空");
        SysSerialNumber serialNumberByCode = querySerialInfo(sysSerialNumber.getSerialCode());
        BaseAssert.isTrue(EmptyUtils.isEmpty(serialNumberByCode),"编码："+sysSerialNumber.getSerialCode()+"已存在");
        sysSerialNumber.setStatus(CoreConstant.STATUS_ENABLE);
        save(sysSerialNumber);
    }

    /**
     * 修改流水号
     * @param serialNumberUpdateDTO
     */
    @Override
    public void updateSerial(SerialNumberUpdateDTO serialNumberUpdateDTO) {
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(serialNumberUpdateDTO) && EmptyUtils.isNotEmpty(serialNumberUpdateDTO.getSerialCode()),"待保存数据为空");
        SysSerialNumber serialNumberByCode = querySerialInfo(serialNumberUpdateDTO.getSerialCode());
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(serialNumberByCode),"编码："+serialNumberUpdateDTO.getSerialCode()+"不存在");
        BeanUtils.copyProperties(serialNumberUpdateDTO,serialNumberByCode);
        serialNumberByCode.setUpdateTime(new Date());
        serialNumberByCode.setUpdateUser(BaseUserUtil.getCurrentUser().getUserCode());
        saveOrUpdate(serialNumberByCode);
    }

    /**
     * 查询流水号
     * @param serialCode
     * @return
     */
    @Override
    public SysSerialNumber querySerialInfo(String serialCode) {
        SysSerialNumber sysSerialNumber = getOne(Wraps.<SysSerialNumber>lbQ().eq(SysSerialNumber::getSerialCode, serialCode)
                .eq(SysSerialNumber::getDelFlag, CoreConstant.FLAG_NORMAL)
                .eq(SysSerialNumber::getStatus, CoreConstant.STATUS_ENABLE));
        return sysSerialNumber;
    }

    /**
     * 删除流水号
     * @param serialCodes
     */
    @Override
    public void batchDeleteSerial(List<String> serialCodes){
        BaseAssert.isTrue(EmptyUtils.isNotEmpty(serialCodes),"参数[serialCodes]为空");
        update(Wraps.<SysSerialNumber>lbU()
                .set(SysSerialNumber::getDelFlag,CoreConstant.FLAG_DELETE)
                .set(SysSerialNumber::getUpdateTime,new Date())
                .set(SysSerialNumber::getUpdateUser,BaseUserUtil.getCurrentUser().getUserCode())
                .in(SysSerialNumber::getSerialCode,serialCodes));
    }

    /**
     * 生成编码
     * @param list
     * @return
     */
    @Override
    public Map<String, List<String>> getSerialList(List<SerialNumberRequest> list) {
        log.info("SerialNumberService.getSerialList:{}", JSON.toJSONString(list));
        BaseAssert.notEmpty(list,"生成编码必要参数不能为空");
        Map<String, Integer> generatorMap = list.stream().peek(v ->
                {
                    if (Objects.isNull(v.getNumber())) {
                        v.setNumber(1);
                    }
                }).filter(vo -> StringUtils.isNotBlank(vo.getSerialCode()))
                .collect(Collectors.toMap(SerialNumberRequest::getSerialCode, SerialNumberRequest::getNumber, (v1, v2) -> v1));
        BaseAssert.isTrue(generatorMap.size() == list.size(), "生成编码配置key重复");
        List<SysSerialNumber> sysSerialNumberList = list(Wraps.<SysSerialNumber>lbQ().in(SysSerialNumber::getSerialCode, generatorMap.keySet())
                .eq(SysSerialNumber::getDelFlag, CoreConstant.FLAG_NORMAL)
                .eq(SysSerialNumber::getStatus, CoreConstant.STATUS_ENABLE));
        BaseAssert.isTrue(sysSerialNumberList.size() == generatorMap.size(), "生成编码配置无效或不存在");
        Map<String, List<String>> resultMap = new HashMap<>();
        String userCode = "admin";
        try{
            userCode = BaseUserUtil.getCurrentUser().getUserCode();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        String finalUserCode = userCode;
        sysSerialNumberList.forEach(vo -> {
            Integer count = generatorMap.get(vo.getSerialCode());
            List<StringBuilder> serialList = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                serialList.add(new StringBuilder());
            }
            String serialRule = vo.getSerialRule();
            Pattern pattern = Pattern.compile("(\\{[a-zA-Z]+\\([a-zA-Z0-9]+\\)\\})");
            Matcher matcher = pattern.matcher(serialRule);
            String ruleNO = getRuleNO(serialRule);
            String ruleDate = getCurrRuleDate(serialRule);
            String ruleParams = getRuleParams(serialRule);
            SerialIndexDto serialIndexDto = new SerialIndexDto();
            serialIndexDto.setSerialCode(vo.getSerialCode());
            serialIndexDto.setSerialDate(ruleDate);
            serialIndexDto.setSerialParam(ruleParams);
            serialIndexDto.setSerialNumCount(ruleNO);
            List<Integer> indexList = serialIndexService.getSerialIndex(serialIndexDto,count);
            while (matcher.find()) {
                String group = matcher.group();
                if (group.startsWith("{PARAMS(")) {
                    for (int i = 0; i < count; i++) {
                        serialList.get(i).append(ruleParams);
                    }
                    continue;
                }
                if (group.startsWith("{DATE(")) {
                    for (int i = 0; i < count; i++) {
                        serialList.get(i).append(ruleDate);
                    }
                    continue;
                }
                if (group.startsWith("{NO(")) {
                    for (int i = 0; i < count; i++) {
                        serialList.get(i).append(zeroFill(indexList.get(i), new Integer(ruleNO)));
                    }
                    continue;
                }
                break;
            }
            resultMap.put(vo.getSerialCode(), serialList.stream().map(StringBuilder::toString).collect(Collectors.toList()));
            update(Wraps.<SysSerialNumber>lbU()
                    .set(SysSerialNumber::getSerialNum, vo.getSerialNum() + count)
                    .set(SysSerialNumber::getUpdateTime, new Date())
                    .set(SysSerialNumber::getUpdateUser, finalUserCode)
                    .eq(SysSerialNumber::getId, vo.getId())
            );
        });

        return resultMap;
    }

    public static String getRuleNO(String codesRule) {
        Pattern pattern = Pattern.compile("\\{NO\\(([0-9]+)\\)\\}");
        Matcher matcher = pattern.matcher(codesRule);
        boolean is = matcher.find();
        if (is) {
            return matcher.group(1);
        }
        return "";
    }

    public static String getCurrRuleDate(String codesRule) {
        Pattern pattern = Pattern.compile("\\{DATE\\(([a-zA-Z]+)\\)\\}");
        Matcher matcher = pattern.matcher(codesRule);
        boolean is = matcher.find();
        if (!is) {
            return "";
        }
        String group = matcher.group(1);
        return new SimpleDateFormat(group).format(new Date());
    }

    public static String getRuleParams(String codesRule) {
        Pattern pattern = Pattern.compile("\\{PARAMS\\(([a-zA-Z]+)\\)\\}");
        Matcher matcher = pattern.matcher(codesRule);
        boolean is = matcher.find();
        if (is) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * 在前填充0
     * @param seq
     * @param codeLengthExcludePrefix
     * @return
     */
    public static String zeroFill(int seq, int codeLengthExcludePrefix) {
        StringBuilder seqStr = new StringBuilder().append(seq);
        if (seqStr.length() < codeLengthExcludePrefix) {
            for (int i = seqStr.length(); i < codeLengthExcludePrefix; i++) {
                seqStr.insert(0, '0');
            }
        }
        return seqStr.toString();
    }
}

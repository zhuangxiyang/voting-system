package com.vote.auth.controller;

import cn.hutool.core.util.EnumUtil;
import com.vote.common.dto.Res;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.util.Map;

@RestController
public class ErrorController extends BasicErrorController {

    public ErrorController(ErrorAttributes ErrorAttributes){
        super(ErrorAttributes,new ErrorProperties());
    }


    /**
     * produces 设置返回的数据类型：application/json
     *
     *@paramrequest请求
     *@return自定义的返回实体类
     */
    @Override
    @RequestMapping(value="",produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String,Object>> error (HttpServletRequest request){
        Map<String,Object> body = getErrorAttributes(request,isIncludeStackTrace(request,MediaType.ALL));
        HttpStatus httpStatus;
        httpStatus=HttpStatus.OK;
        return new ResponseEntity(Res.checkFail((Integer) body.get("status"),body.get("error"),body.get("message").toString()),httpStatus);
    }

}
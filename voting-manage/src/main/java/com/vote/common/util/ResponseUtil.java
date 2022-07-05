package com.vote.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vote.common.dto.Res;
import com.vote.common.exception.AuthException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 响应工具类
 *
 */
public interface ResponseUtil<T> {

    /**
     * webflux方式返回
     * @param webFilterExchange
     * @param objectMapper
     * @param data
     * @return
     */
    default Mono<Void> getResponse(WebFilterExchange webFilterExchange, ObjectMapper objectMapper,T data){
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        DataBuffer bodyDataBuffer = null;
        try {
            bodyDataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(Res.success(data)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new AuthException("转换返回结果错误");
        }
        return response.writeWith(Mono.just(bodyDataBuffer));
    }

    /**
     * web方式返回
     * @param response
     * @param objectMapper
     * @param data
     * @throws IOException
     */
    default  void getSuccessResponseWeb(HttpServletResponse response,ObjectMapper objectMapper,T data)throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Res.success(data)));
    }

    /**
     * web方式返回
     * @param response
     * @param objectMapper
     * @param msg
     * @throws IOException
     */
    default  void getFailResponseWeb(HttpServletResponse response,ObjectMapper objectMapper,String msg)throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Res.result(-1,null,msg)));
    }
}

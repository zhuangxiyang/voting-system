package com.vote.auth.handler.webflux;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vote.common.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

/**
 * 退出成功处理逻辑
 *
 */
public class WebfluxLogoutSuccessHandler implements ServerLogoutSuccessHandler, ResponseUtil<String> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return getResponse(webFilterExchange,objectMapper,"退出成功");
    }
}

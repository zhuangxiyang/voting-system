package com.vote.auth.handler.webflux;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vote.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

public class WebfluxLoginFailureHandler implements ServerAuthenticationFailureHandler, ResponseUtil<String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        String msg = null;
        if (exception instanceof BadCredentialsException) {
            msg = "账号或密码错误";
        } else {
            msg = "认证失败:"+exception.getMessage();
        }
        return getResponse(webFilterExchange,objectMapper,msg);
    }
}

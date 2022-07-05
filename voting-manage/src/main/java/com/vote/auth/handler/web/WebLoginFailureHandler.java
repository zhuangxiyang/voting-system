package com.vote.auth.handler.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vote.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebLoginFailureHandler implements AuthenticationFailureHandler, ResponseUtil<String> {


    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String msg = null;
        if (exception instanceof BadCredentialsException) {
            msg = "账号或密码错误";
        } else {
            msg = exception.getMessage();
        }
        response.setStatus(200);
        getFailResponseWeb(response,objectMapper,msg);
    }
}

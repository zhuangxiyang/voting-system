package com.vote.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vote.common.dto.BaseUser;
import com.vote.common.dto.Res;
import com.vote.common.exception.BaseException;
import com.vote.common.util.TokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtVerifyFilter extends BasicAuthenticationFilter {

    public JwtVerifyFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try{
            String header = request.getHeader("Authorization");
            if(request.getRequestURI().contains("swagger") || request.getRequestURI().contains("api-docs")
                    || request.getRequestURI().contains("saveVotingRecord") || request.getRequestURI().contains("getCandidateRecordPage")){
                chain.doFilter(request, response);
                return;
            }
            //没有登录
            if (header == null || !header.startsWith("Bearer ")) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = response.getWriter();
                Res<Object> res = Res.checkFail(HttpServletResponse.SC_FORBIDDEN,"", "请登录");
                out.write(new ObjectMapper().writeValueAsString(res));
                out.flush();
                out.close();
                return;
            }
            //登录之后从token中获取用户信息
            String token = header.replace("Bearer ","");
            BaseUser baseUser = TokenUtil.getUser();
            if (!TokenUtil.judgeTokenValid(String.valueOf(baseUser.getUserCode()), token)) {
                throw new BaseException("token过期，请重新登录", -98);
            }
            if (baseUser != null) {
                Authentication authResult = new UsernamePasswordAuthenticationToken
                        (baseUser.getUserCode(),null,null);
                SecurityContextHolder.getContext().setAuthentication(authResult);
                chain.doFilter(request, response);
            }
        }catch (BaseException ex){
            PrintWriter out = response.getWriter();
            Res<Object> res = Res.checkFail(ex.getCode(),"", ex.getMessage());
            out.write(new ObjectMapper().writeValueAsString(res));
            out.flush();
            out.close();
            return;
        }

    }
}


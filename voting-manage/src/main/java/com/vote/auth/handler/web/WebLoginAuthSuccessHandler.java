package com.vote.auth.handler.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vote.auth.properties.AuthServerProperties;
import com.vote.auth.token.BaseUserDetail;
import com.vote.common.dto.TokenEntity;
import com.vote.common.exception.AuthException;
import com.vote.common.util.ResponseUtil;
import com.vote.common.util.TokenUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class WebLoginAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements ResponseUtil<Map> {

	@Autowired
	private DefaultTokenServices defaultTokenServices;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenStore authTokenStore;

    @Autowired
    private AuthServerProperties authServerProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Map<String,Object> result = createToken(request,authentication);
        getSuccessResponseWeb(response,objectMapper,result);
        logger.info("登录成功");
    }

    /**
     * 创建token
     * @param request
     * @param authentication
     */
    private Map<String, Object> createToken(HttpServletRequest request, Authentication authentication){

        TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, "", null,"password");

        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(new BaseClientDetails());

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
        defaultTokenServices.setTokenStore(authTokenStore);
        logger.info("==="+authentication.getPrincipal());
        defaultTokenServices.setAccessTokenValiditySeconds(authServerProperties.getTokenValid());
        //开启刷新功能
        if(authServerProperties.getStartRefresh()) {
            defaultTokenServices.setRefreshTokenValiditySeconds(authServerProperties.getRefreshTokenValid());
        }

        OAuth2AccessToken token = defaultTokenServices.createAccessToken(oAuth2Authentication);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> result = new HashMap<>();
        result.put("access_token", token.getValue());
        result.put("token_Expiration", sdf.format(token.getExpiration()));
        //开启刷新功能
        if(authServerProperties.getStartRefresh()) {
            //获取刷新Token
            DefaultExpiringOAuth2RefreshToken refreshToken = (DefaultExpiringOAuth2RefreshToken) token.getRefreshToken();
            result.put("refresh_token", refreshToken.getValue());
            result.put("refresh_token_Expiration", sdf.format(refreshToken.getExpiration()));
        }
        logger.debug("token:"+token.getValue());
        //判断token的和方法性
        String userCode = String.valueOf(((BaseUserDetail)authentication.getPrincipal()).getBaseUser().getUserCode());
        if(!TokenUtil.pushToken(userCode,token.getValue(),token.getExpiration(),authServerProperties.getMaxClient())){
            throw new AuthException("登录限制，同时登录人数过多");
        }
        return result;
    }

}

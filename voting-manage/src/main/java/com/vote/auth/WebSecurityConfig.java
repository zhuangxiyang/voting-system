package com.vote.auth;

import com.vote.auth.filter.JwtVerifyFilter;
import com.vote.auth.handler.web.WebLoginAuthSuccessHandler;
import com.vote.auth.handler.web.WebLoginFailureHandler;
import com.vote.auth.handler.web.WebLogoutHandler;
import com.vote.auth.handler.web.WebLogoutSuccessHandler;
import com.vote.auth.service.UsernameUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsUtils;

/**
 * 配置spring security
 * ResourceServerConfig 是比SecurityConfig 的优先级低的
 *
 *
 */
@Configuration
@EnableWebSecurity
@Order(1)
@EnableGlobalMethodSecurity(securedEnabled = true)  //开启权限控制的注解支持,securedEnabled表示SpringSecurity内部的权限控制注解开关
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	/**
	 * 用户详情业务实现
	 */
	@Autowired
	private UsernameUserDetailService userDetailsService;

	/**
	 * 重新实例化bean
	 */
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 由于使用的是JWT，我们这里不需要csrf
		http.cors().
				and().csrf().disable()
				.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll().and()
				.logout().addLogoutHandler(getLogoutHandler()).logoutSuccessHandler(getLogoutSuccessHandler()).and()
				.addFilterBefore(getUsernameLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilter(new JwtVerifyFilter(super.authenticationManager()))
				.authorizeRequests().antMatchers("/oauth/**").permitAll().and()
				.authorizeRequests().antMatchers("/logout/**").permitAll().and()
				.authorizeRequests().antMatchers("/pub-key/jwt.json").permitAll().and()
				.authorizeRequests().antMatchers("/js/**","/favicon.ico").permitAll().and()
				.authorizeRequests().antMatchers("/v2/api-docs/**","/webjars/**","/swagger-resources/**","/*.html").permitAll().and()
			 // 其余所有请求全部需要鉴权认证
			.authorizeRequests().anyRequest().authenticated()
			;
	}


	/**
	 * 用户验证
	 */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // 设置userDetailsService
        provider.setUserDetailsService(userDetailsService);
        // 禁止隐藏用户未找到异常
        provider.setHideUserNotFoundExceptions(false);
        // 使用BCrypt进行密码的hash
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	/**
	 * 账号密码登录
	 * @return
	 */
	@Bean
	public UsernamePasswordAuthenticationFilter getUsernameLoginAuthenticationFilter(){
		UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
		try {
			filter.setAuthenticationManager(this.authenticationManagerBean());
		} catch (Exception e) {
			e.printStackTrace();
		}
		filter.setAuthenticationSuccessHandler(getLoginSuccessAuth());
		filter.setAuthenticationFailureHandler(getLoginFailure());
		return filter;
	}


	@Bean
	public WebLoginAuthSuccessHandler getLoginSuccessAuth(){
		WebLoginAuthSuccessHandler myLoginAuthSuccessHandler = new WebLoginAuthSuccessHandler();
		return myLoginAuthSuccessHandler;
	}

	@Bean
	public WebLoginFailureHandler getLoginFailure(){
		WebLoginFailureHandler myLoginFailureHandler = new WebLoginFailureHandler();
		return myLoginFailureHandler;
	}

	@Bean
	public LogoutHandler getLogoutHandler(){
		WebLogoutHandler myLogoutHandler = new WebLogoutHandler();
		return myLogoutHandler;
	}

	@Bean
	public LogoutSuccessHandler getLogoutSuccessHandler(){
		WebLogoutSuccessHandler logoutSuccessHandler = new WebLogoutSuccessHandler();
		return logoutSuccessHandler;
	}

}

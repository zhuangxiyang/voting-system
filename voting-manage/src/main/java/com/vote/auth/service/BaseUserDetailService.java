package com.vote.auth.service;

import com.vote.auth.token.BaseUserDetail;
import com.vote.common.dto.BaseUser;
import com.vote.common.exception.AuthException;
import com.vote.common.util.Constant;
import com.vote.server.dto.resp.VoteUserRespDTO;
import com.vote.server.entity.Permission;
import com.vote.server.entity.VoteUser;
import com.vote.server.service.VoteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseUserDetailService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 用户业务接口
     */
    @Resource
    protected VoteUserService userService;


    @Autowired
    private RedisTemplate<String, Permission> permissionRedisTemplate;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if(attributes==null){
            throw new AuthException("获取不到当先请求");
        }
        HttpServletRequest request = attributes.getRequest();

        VoteUserRespDTO userInfo = getUser(username);

        List<GrantedAuthority> authorities = new ArrayList<>() ;
//        if(EmptyUtils.isNotEmpty(userInfo.getId())){
//            //查询角色列表
//            List<Role> roles = roleApi.listByUser(userInfo.getId()).getResult();
//            roles.forEach(role->{
//                //只存储角色，所以不需要做区别判断
//                authorities.add(new SimpleGrantedAuthority(role.getRoleCode()));
//                List<Permission> permissions = permissionApi.listByRole(role.getId()).getResult();
//                //存储权限到redis集合,保持颗粒度细化，当然也可以根据用户存储
//                storePermission(permissions,role.getRoleCode());
//            });
//        }
        // 返回带有用户权限信息的User
        org.springframework.security.core.userdetails.User user =
                new org.springframework.security.core.userdetails.User(
                        userInfo.getUserCode(),
                        userInfo.getPassword(),
                        isActive(userInfo.getLoginStatus()),
                        true,
                        true,
                        true, authorities);
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(userInfo,baseUser);
        baseUser.setId(userInfo.getId());
        return new BaseUserDetail(baseUser, user);
    }

    /**
     * 存储权限
     * @param permissions
     */
    private void storePermission(List<Permission> permissions,String roleCode){
        String redisKey = Constant.PERMISSIONS +roleCode;
        // 清除 Redis 中用户的角色
        permissionRedisTemplate.delete(redisKey);
        permissions.forEach(permission -> {
            permissionRedisTemplate.opsForList().rightPush(redisKey,permission);
        });
    }
    /**
     * 获取用户
     * @param userName
     * @return
     */
    protected abstract VoteUserRespDTO getUser(String userName) ;

    /**
     * 是否有效的
     * @param active
     * @return
     */
    private boolean isActive(Integer active){
        if(1==active){
            return true;
        }
        return false;
    }

}
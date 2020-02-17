package com.hyl.config.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hyl.config.jwt.JwtToken;
import com.hyl.config.jwt.JwtUtil;
import com.hyl.mapper.UserMapper;
import com.hyl.pojo.User;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Service
public class MyRealm extends AuthorizingRealm {

    private static final Logger LOGGER = LogManager.getLogger(MyRealm.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 当用户检测权限的时候才会调用此方法，
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        try{
            User user = JwtUtil.getUserFromToken(principals.toString(),JwtUtil.SECRET);
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("username",user.getUsername());
            queryWrapper.eq("password",user.getPassword());
            User user1 = userMapper.selectOne(queryWrapper);
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            simpleAuthorizationInfo.addRole(user1.getRole());
            Set<String> permission = new HashSet<>(Arrays.asList(user1.getPermission().split("\\,")));
            simpleAuthorizationInfo.addStringPermissions(permission);
            return simpleAuthorizationInfo;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * 默认使用此方法进行用户名正确是否验证，错误抛出异常即可
     * @param auth
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token =(String) auth.getCredentials();
        // 解密活的username,用于和数据库进行对比
        try{
            User user = JwtUtil.getUserFromToken(token,JwtUtil.SECRET);
            if(user == null){
                throw new AuthenticationException("非法token");
            }
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("username",user.getUsername());
            queryWrapper.eq("password",user.getPassword());
            User user1 = userMapper.selectOne(queryWrapper);
            if(user1 ==null){
                throw  new AuthenticationException("User didn't existed!");
            }
            if(! JwtUtil.verify(token)){
                throw new AuthenticationException("Username or password error");
            }
            return new SimpleAuthenticationInfo(token,token,"my_realm");
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}

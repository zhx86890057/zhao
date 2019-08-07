package com.zhao.upms.web.service.impl;

import com.zhao.dao.domain.SysUser;
import com.zhao.dao.mapper.SysUserMapper;
import com.zhao.upms.common.api.CommonException;
import com.zhao.upms.common.api.ResultCode;
import com.zhao.upms.web.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SysUserMapper sysUserMapper;


    @Override
    public SysUser getByUsername(String username) {
        SysUser sysUser = sysUserMapper.selectByUsername(username);
        return sysUser;
    }

    @Override
    public SysUser register(SysUser sysUser) {
        sysUser.setCreateTime(new Date());
        sysUser.setStatus(1);
        //查询是否有相同用户名的用户
        SysUser user = sysUserMapper.selectByUsername(sysUser.getUsername());
        if (user != null) {
            throw new CommonException(ResultCode.USER_EXISTS);
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(sysUser.getPassword());
        sysUser.setPassword(encodePassword);
        int line = sysUserMapper.insert(sysUser);
        return sysUser;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

//            updateLoginTimeByUsername(username);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public String refreshToken(String oldToken) {
        return null;
    }
}

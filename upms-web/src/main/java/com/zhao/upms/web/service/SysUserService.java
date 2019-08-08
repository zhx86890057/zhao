package com.zhao.upms.web.service;

import com.zhao.dao.domain.SysUser;

import java.util.List;


public interface SysUserService {

    /**
     * 根据用户名获取后台管理员
     */
    SysUser getByUsername(String username);

    /**
     * 注册功能
     */
    SysUser register(SysUser sysUser);

    /**
     * 登录功能
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT的token
     */
    String login(String username, String password);

    /**
     * 刷新token的功能
     * @param oldToken 旧的token
     */
    String refreshToken(String oldToken);

}

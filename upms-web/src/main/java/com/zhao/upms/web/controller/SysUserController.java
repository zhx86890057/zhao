package com.zhao.upms.web.controller;

import com.zhao.dao.domain.SysUser;
import com.zhao.upms.common.api.CommonResult;
import com.zhao.upms.web.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@Api(tags = "UserController", description = "用户管理")
@RequestMapping("/admin")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult<SysUser> register(@Validated @RequestBody SysUser sysUser, BindingResult result) {
        SysUser sysUser1 = sysUserService.register(sysUser);
        return CommonResult.success(sysUser);
    }

    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult login(@Validated @RequestBody SysUser sysUser, BindingResult result) {
        String token = sysUserService.login(sysUser.getUsername(), sysUser.getPassword());
        if (token == null) {
            return CommonResult.failed("用户名或密码错误");
        }
        return CommonResult.success(token);
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/token/refresh", method = RequestMethod.GET)
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = sysUserService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed();
        }
        return CommonResult.success(refreshToken);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult getAdminInfo(Principal principal) {
        String username = principal.getName();
        SysUser sysUser = sysUserService.getByUsername(username);
        return CommonResult.success(sysUser);
    }
}

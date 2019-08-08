package com.zhao.upms.web.controller;

import com.zhao.dao.domain.SysUser;
import com.zhao.upms.common.api.CommonException;
import com.zhao.upms.common.api.CommonResult;
import com.zhao.upms.common.api.ResultCode;
import com.zhao.upms.web.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ben on 2019/1/16.
 */
@RestController
public class LoginEndpoint{

    @Autowired
    @Qualifier("consumerTokenServices")
    ConsumerTokenServices consumerTokenServices;
    @Autowired
    private TokenEndpoint tokenEndpoint;
    @Autowired
    private SysUserService sysUserService;
    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/user/loginPwd")
    public CommonResult<OAuth2AccessToken> loginPwd(String username, String password) {
        try {
            Authentication principal = new UsernamePasswordAuthenticationToken("auth-server","123456",null);
            Map<String, String> param = new LinkedHashMap<>();
            param.put("grant_type", "password");
            param.put("username", username);
            param.put("password", password);
            param.put("scope", "read");
            ResponseEntity<OAuth2AccessToken> responseEntity =  tokenEndpoint.postAccessToken(principal,param);
            OAuth2AccessToken token  = responseEntity.getBody();
            return CommonResult.success(token);
        }catch (Exception ex){
            ex.printStackTrace();
            throw new CommonException(ResultCode.LOGIN_ERROR);
        }
    }

    /**
     * 获取用户信息
     * @param user
     * @return
     */
    @RequestMapping(
            value = {"/user/info"},
            method = {RequestMethod.POST}
    )
//    @PreAuthorize("hasRole('test')")
    public CommonResult<SysUser> userInfo(Principal user){
        String username = user.getName();
        SysUser sysUser = sysUserService.getByUsername(username);
        return CommonResult.success(sysUser);
    }

    /**
     * 刷新token
     * @param refreshToken
     * @return
     */
    @PostMapping("/user/refreshToken")
    public CommonResult<OAuth2AccessToken> refreshToken(String refreshToken) {
        try {
            Authentication principal = new UsernamePasswordAuthenticationToken("powercloud-platform","5788dc4670f00b25521360d1",null);
            Map<String, String> param = new LinkedHashMap<>();
            param.put("grant_type", "refresh_token");
            param.put("refresh_token", refreshToken);
            ResponseEntity<OAuth2AccessToken> responseEntity = tokenEndpoint.postAccessToken(principal,param);
            OAuth2AccessToken token  = responseEntity.getBody();
            return CommonResult.success(token);
        } catch (HttpRequestMethodNotSupportedException e) {
            e.printStackTrace();
            throw new CommonException(ResultCode.FAILED.getCode(), "刷新token失败");
        }
    }


    /**
     * 退出登录
     * @return
     */
    @PostMapping("/user/logout")
    public CommonResult<String> logout(Principal user, HttpServletRequest request) {
//        String header = request.getHeader("Authorization");
//        String access_token = null;
//        if (StringUtils.isNotBlank(header)) {
//            SysUser userInfo = sysUserService.getByUsername(user.getName());
//            access_token = header.substring(OAuth2AccessToken.BEARER_TYPE.length() + 1);
//            if (consumerTokenServices.revokeToken(access_token)){
//                userService.logout(user.getName());
//            }
//        }
//        return CommonResult.success(access_token);

        String access_token = request.getParameter("access_token");
        consumerTokenServices.revokeToken(access_token);
        return CommonResult.success(null);
    }
}

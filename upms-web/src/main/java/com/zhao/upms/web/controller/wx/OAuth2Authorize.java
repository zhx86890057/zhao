package com.zhao.upms.web.controller.wx;

import com.zhao.upms.web.service.impl.WxAPI;
import com.zhao.upms.web.wxBean.WxAccessToken;
import com.zhao.upms.web.wxBean.WxAuthCorpInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户在不告知第三方自己的帐号密码情况下，通过授权方式，让第三方服务可以获取自己的资源信息 网页授权登陆 第三方应用oauth2链接
 *
 */
@RestController
@RequestMapping(value = "/wx/auth")
@Api(tags = "OAuth2Authorize", description = "授权登录")
@Slf4j
public class OAuth2Authorize {

    @Autowired
    private WxAPI wxAPI;

    /**
     * 三方应用oauth2链接回调
     * @param code
     * @return
     */
    @GetMapping("/callback3rd")
    public String callback3rd(@RequestParam(name = "code") String code){
        String suiteAccessToken = "8qibKlIP0fBCR1FvfoxLTrQuzGRXmzYQAyjdINfo1GR9kfIFmI16aUMqMEGS29" +
                "-duhNhbDY7G7_4iuBz5BEmVuzYxOJ28QsNsdrMNNQOm2H5pTZnYJVvo1VzXi77K2io";
        return wxAPI.getUserInfo3rd(suiteAccessToken, code);
    }

    /**
     * 企业oauth2链接回调
     * @param code
     * @return
     */
    @GetMapping("/callbackCorp")
    public String callbackCorp(@RequestParam(name = "code") String code){
        String suiteAccessToken = "8qibKlIP0fBCR1FvfoxLTrQuzGRXmzYQAyjdINfo1GR9kfIFmI16aUMqMEGS29" +
                "-duhNhbDY7G7_4iuBz5BEmVuzYxOJ28QsNsdrMNNQOm2H5pTZnYJVvo1VzXi77K2io";
        return wxAPI.getUserInfo3rd(suiteAccessToken, code);
    }
}

package com.zhao.upms.web.controller.wx;

import com.zhao.upms.common.api.CommonResult;
import com.zhao.upms.web.constant.WxAppConfigs;
import com.zhao.upms.web.service.impl.WxAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * *   企业授权应用   方式一：从服务商网站发起
 * *
 * *   @author   wison
 */
@RestController
@RequestMapping(value = "/wx/install")
@Slf4j
public class ProviderAuthorize {
    @Autowired
    private WxAPI wxAPI;

    /**
     * *   第三方服务商在自己的网站中放置“企业微信应用授权”的入口，引导企业微信管理员进入应用授权页
     * *   @param   suiteId 应用id
     */
    @RequestMapping(value = "/buildUrl")
    public CommonResult buildUrl(@RequestParam String suiteId) {
        WxAppConfigs.AppConfig appConfig = WxAppConfigs.getAppConfig(suiteId);
        if (appConfig == null) {
            log.error("未找到对应suiteId={}的配置，请核实！", suiteId);
            return CommonResult.failed(String.format("未找到对应suiteId=[%d]的配置，请核实！", suiteId));
        }
        String baseUrl = "http://2panth.natappfree.cc";
        String redirectUri = baseUrl + "/wx/install/installCallback?suiteId=" + suiteId;
        //查询第三方应用，获取预授权码
        String preAuthCode = wxAPI.getPreAuthCode(appConfig.getSuiteAccessToken());
        wxAPI.setSessionInfo(appConfig.getSuiteAccessToken(), preAuthCode, 1);
        String url = wxAPI.installURL(suiteId, preAuthCode, redirectUri, null);
        return CommonResult.success(url);

    }

    /**
     * *   引导授权回调   根据临时授权码（10分钟有效），换取永久授权码
     * *
     */
    @RequestMapping(value = "/installCallback")
    public String installCallback(@RequestParam("auth_code")String authCode,
                                @RequestParam("expires_in")Integer expiresIn,
                                @RequestParam(value = "state", required = false)String state,
                                @RequestParam String suiteId) {
        WxAppConfigs.AppConfig appConfig = WxAppConfigs.getAppConfig(suiteId);
        if (appConfig == null) {
            log.info("未找到对应suiteId={}的配置，请核实！", suiteId);
            return String.format("未找到对应suiteId=[%d]的配置，请核实！", suiteId);
        }
        String permanentCode = wxAPI.getPermanentCode(authCode, appConfig.getSuiteAccessToken());
        return permanentCode;
    }

}

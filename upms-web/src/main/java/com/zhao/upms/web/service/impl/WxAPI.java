package com.zhao.upms.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.zhao.upms.common.api.CommonException;
import com.zhao.upms.common.api.ResultCode;
import com.zhao.upms.web.constant.WxAppConfigs;
import com.zhao.upms.web.constant.WxCpApiPathConsts;
import com.zhao.upms.web.constant.WxCpErrorMsgEnum;
import com.zhao.upms.web.wxBean.WxAccessToken;
import com.zhao.upms.web.wxBean.WxAuthCorpInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.zhao.upms.web.constant.WxCpApiPathConsts.OAuth2.QRCONNECT_URL;
import static com.zhao.upms.web.constant.WxCpApiPathConsts.OAuth2.URL_OAUTH2_AUTHORIZE;
import static com.zhao.upms.web.constant.WxCpConsts.OAuth2Scope.SNSAPI_PRIVATEINFO;
import static com.zhao.upms.web.constant.WxCpConsts.OAuth2Scope.SNSAPI_USERINFO;

@Service
@Slf4j
public class WxAPI {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取第三方应用凭证
     * @param appConfig
     * @return
     */
    public String getSuiteAccessToken(WxAppConfigs.AppConfig appConfig){
        Map<String, String> params= new HashMap<>();
        params.put("suite_id", appConfig.getSuiteID());
        params.put("suite_secret", appConfig.getSecret());
        params.put("suite_ticket", appConfig.getSuiteTicket());
        JSONObject jsonObject =
                restTemplate.postForObject(WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_SUITE_TOKEN),
        JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        String suiteAccussToken = jsonObject.getString("suite_access_token");
        Integer expiresIn = jsonObject.getIntValue("expires_in");
        appConfig.updateSuiteAccessToken(suiteAccussToken, expiresIn);
        return suiteAccussToken;
    }

    /**
     * 获取预授权码
     * @param suiteAccussToken
     * @return
     */
    public String getPreAuthCode(String suiteAccussToken){
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_PRE_AUTH_CODE) + "?suite_access_token={1}";
        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class, suiteAccussToken);
        checkErrCode(jsonObject);
        String preAuthCode = jsonObject.getString("pre_auth_code");
        return preAuthCode;
    }

    /**
     * 设置授权配置
     * @param suiteAccussToken
     * @param preAuthCode
     */
    public void setSessionInfo(String suiteAccussToken, String preAuthCode){
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.SET_SESSION_INFO) + suiteAccussToken;
        Map<String, Object> params= new HashMap<>();
        params.put("pre_auth_code", preAuthCode);
        Map<String, Integer> sessionMap = new HashMap<>();
        sessionMap.put("auth_type", 1);
        params.put("session_info", sessionMap);
        JSONObject jsonObject = restTemplate.postForObject(url, JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
    }

    /**
     * 构造第三方网页授权链接
     * @param redirectUri
     * @param state
     * @param scope
     * @return
     */
    public String build3rdAuthUrl(String suiteId, String redirectUri, String state, String scope) {
        StringBuilder url = new StringBuilder(URL_OAUTH2_AUTHORIZE);
        url.append("?appid=").append(suiteId);
        url.append("&redirect_uri=").append(URLEncoder.encode(redirectUri));
        url.append("&response_type=code");
        url.append("&scope=").append(scope);
        if (state != null) {
            url.append("&state=").append(state);
        }
        url.append("#wechat_redirect");
        return url.toString();
    }

    public static void main(String[] args) {
        String snsapi_base = new WxAPI().build3rdAuthUrl("wwc5d50093b12345b8", "http://4hwb9r.natappfree.cc/wx/auth/test", "111", "snsapi_base");
        System.out.println(snsapi_base);
        String snsapi_base2 = new WxAPI().buildCorpAuthUrl("wwc5d50093b12345b8", "http://4hwb9r.natappfree.cc/wx/auth/test", "111", "snsapi_base");
        System.out.println(snsapi_base2);
    }
    /**
     * 构造企业oauth2链接
     * @param redirectUri
     * @param state
     * @param scope
     * @return
     */
    public String buildCorpAuthUrl(String suiteId, String redirectUri, String state, String scope){
        StringBuilder url = new StringBuilder(URL_OAUTH2_AUTHORIZE);
        url.append("?appid=").append(WxAppConfigs.corpId);
        url.append("&redirect_uri=").append(URLEncoder.encode(redirectUri));
        url.append("&response_type=code");
        url.append("&scope=").append(scope);
        if (SNSAPI_PRIVATEINFO.equals(scope) || SNSAPI_USERINFO.equals(scope)) {
            url.append("&agentid=").append(suiteId);
        }
        if (state != null) {
            url.append("&state=").append(state);
        }
        url.append("#wechat_redirect");
        return url.toString();
    }

    /**
     * <pre>
     * 构造第三方使用网站应用授权登录的url.
     * 详情请见: <a href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN">网站应用微信登录开发指南</a>
     * URL格式为：https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
     * </pre>
     *
     * @param redirectURI 用户授权完成后的重定向链接
     * @param scope       应用授权作用域，拥有多个作用域用逗号（,）分隔，网页应用目前仅填写snsapi_login即可
     * @param state       非必填，用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
     * @return url
     */
    public String buildQrConnectUrl(String suitId, String redirectURI, String scope, String state){
        return String.format(QRCONNECT_URL, suitId,URLEncoder.encode(redirectURI), scope, state);
    }

    /**
     * 使用临时授权码换取授权方的永久授权码
     * @param authCode
     * @return
     */
    public String getPermanentCode(String authCode,String suiteAccussToken) {
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_PERMANENT_CODE) + suiteAccussToken;
        Map<String, String> params= new HashMap<>();
        params.put("auth_code", authCode);
        JSONObject jsonObject =
                restTemplate.postForObject(url, JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        return jsonObject.getString("permanent_code");
    }

    /**
     * 通过永久授权码换取企业微信的授权信息
     * @param authCorpid
     * @param permanentCode
     */
    public WxAuthCorpInfo getAuthInfo(String authCorpid, String permanentCode){
        Map<String, String> params= new HashMap<>();
        params.put("auth_corpid", authCorpid);
        params.put("permanent_code", permanentCode);
        JSONObject jsonObject =
                restTemplate.postForObject(WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_AUTH_INFO),
                        JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        WxAuthCorpInfo wxAuthCorpInfo = jsonObject.getObject("auth_corp_info", WxAuthCorpInfo.class);
        return wxAuthCorpInfo;
    }

    /**
     * 获取企业凭证
     * @param authCorpid
     * @param permanentCode
     * @return
     */
    public WxAccessToken getCorpToken(String authCorpid, String permanentCode){
        Map<String, String> params= new HashMap<>();
        params.put("auth_corpid", authCorpid);
        params.put("permanent_code", permanentCode);
        JSONObject jsonObject =
                restTemplate.postForObject(WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_CORP_TOKEN),
                        JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        String accessToken = jsonObject.getString("access_token");
        int expiresIn = jsonObject.getIntValue("expires_in");
        return new WxAccessToken(accessToken, expiresIn);
    }

    public void checkErrCode(JSONObject jsonObject){
        int errcode = jsonObject.getIntValue("errcode");
        if(errcode != WxCpErrorMsgEnum.CODE_0.getCode()){
            String msgByCode = WxCpErrorMsgEnum.findMsgByCode(errcode);
//            log.error("企业微信返回错误码：{}，错误消息：{}", errcode, msgByCode);
//            throw new CommonException(ResultCode.FAILED.getCode(), msgByCode);
            throw new IllegalArgumentException(msgByCode);
        }
    }
}

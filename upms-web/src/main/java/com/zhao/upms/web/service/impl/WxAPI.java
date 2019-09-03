package com.zhao.upms.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhao.dao.domain.SysUserRole;
import com.zhao.dao.domain.UmsMember;
import com.zhao.dao.domain.WxCorp;
import com.zhao.dao.mapper.SysUserRoleMapper;
import com.zhao.dao.mapper.UmsMemberMapper;
import com.zhao.dao.mapper.WxCorpMapper;
import com.zhao.upms.web.constant.WxAppConfigs;
import com.zhao.upms.web.constant.WxCpApiPathConsts;
import com.zhao.upms.web.constant.WxCpErrorMsgEnum;
import com.zhao.upms.web.wxBean.WxAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.zhao.upms.web.constant.WxCpApiPathConsts.OAuth2.*;
import static com.zhao.upms.web.constant.WxCpConsts.OAuth2Scope.SNSAPI_PRIVATEINFO;
import static com.zhao.upms.web.constant.WxCpConsts.OAuth2Scope.SNSAPI_USERINFO;

@Service
@Slf4j
public class WxAPI {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WxCorpMapper wxCorpMapper;
    @Autowired
    private UmsMemberMapper umsMemberMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

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
        String suiteAccessToken = jsonObject.getString("suite_access_token");
        Integer expiresIn = jsonObject.getIntValue("expires_in");
        appConfig.updateSuiteAccessToken(suiteAccessToken, expiresIn);
        return suiteAccessToken;
    }

    /**
     * 获取预授权码
     * @param suiteAccessToken
     * @return
     */
    public String getPreAuthCode(String suiteAccessToken){
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_PRE_AUTH_CODE) + "?suite_access_token={1}";
        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class, suiteAccessToken);
        System.out.println(jsonObject);
        checkErrCode(jsonObject);
        String preAuthCode = jsonObject.getString("pre_auth_code");
        return preAuthCode;
    }

    /**
     * 设置授权配置
     * @param suiteAccessToken
     * @param preAuthCode
     */
    public void setSessionInfo(String suiteAccessToken, String preAuthCode, int authType){
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.SET_SESSION_INFO) + suiteAccessToken;
        Map<String, Object> params= new HashMap<>();
        params.put("pre_auth_code", preAuthCode);
        Map<String, Integer> sessionMap = new HashMap<>();
        sessionMap.put("auth_type", authType);
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
        String snsapi_base = new WxAPI().build3rdAuthUrl("wwc5d50093b12345b8", "http://2panth.natappfree.cc/wx/auth/test", "111", "snsapi_base");
        System.out.println(snsapi_base);
        String snsapi_base2 = new WxAPI().buildCorpAuthUrl("wwc5d50093b12345b8", "http://2panth.natappfree.cc/wx/auth/callbackCorp", "111", "snsapi_base");
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
     * 企业微信应用授权”的入口，引导企业微信管理员进入应用授权页
     * @param suitId
     * @param preAuthCode
     * @param redirectURI
     * @param state
     */
    public String installURL(String suitId, String preAuthCode, String redirectURI, String state) {
        return String.format(INSTALL, suitId, preAuthCode, URLEncoder.encode(redirectURI), state);
    }

    /**
     * 使用临时授权码换取授权方的永久授权码
     * @param authCode
     * @return
     */
    @Transactional
    public String getPermanentCode(String authCode,String suiteAccessToken) {
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_PERMANENT_CODE) + suiteAccessToken;
        Map<String, String> params= new HashMap<>();
        params.put("auth_code", authCode);
        JSONObject jsonObject = restTemplate.postForObject(url, JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        String permanentCode = jsonObject.getString("permanent_code");
        WxCorp wxCorp = jsonObject.getObject("auht_corp_info", WxCorp.class);
        wxCorp.setPermanentCode(permanentCode);
        int insert = wxCorpMapper.insert(wxCorp);
        if(insert == 0){
            log.error("插入企业信息失败：{}", JSON.toJSONString(wxCorp));
        }
        UmsMember member = jsonObject.getObject("auth_user_info", UmsMember.class);
        int insert2 = umsMemberMapper.insert(member);
        if(insert2 == 0){
            log.error("插入授权管理员失败：{}", JSON.toJSONString(member));
        }
        SysUserRole userRole = SysUserRole.builder().roleId("admin").userId(member.getId()).build();
        int insert3 = sysUserRoleMapper.insert(userRole);
        if(insert3 == 0){
            log.error("插入管理员角色失败：{}", JSON.toJSONString(member));
        }
        return permanentCode;
    }

    /**
     * 通过永久授权码换取企业微信的授权信息
     * @param authCorpid
     * @param permanentCode
     */
    public WxCorp getAuthInfo(String suiteAccessToken, String authCorpid, String permanentCode){
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_AUTH_INFO) + suiteAccessToken;
        Map<String, String> params= new HashMap<>();
        params.put("auth_corpid", authCorpid);
        params.put("permanent_code", permanentCode);
        JSONObject jsonObject = restTemplate.postForObject(url,JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        WxCorp wxCorp = jsonObject.getObject("auth_corp_info", WxCorp.class);
        return wxCorp;
    }

    /**
     * 获取企业凭证
     * @param authCorpid
     * @param permanentCode
     * @return
     */
    public WxAccessToken getCorpToken(String suiteAccessToken, String authCorpid, String permanentCode){
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_CORP_TOKEN) + suiteAccessToken;
        Map<String, String> params= new HashMap<>();
        params.put("auth_corpid", authCorpid);
        params.put("permanent_code", permanentCode);
        JSONObject jsonObject = restTemplate.postForObject(url, JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        String accessToken = jsonObject.getString("access_token");
        int expiresIn = jsonObject.getIntValue("expires_in");
        return new WxAccessToken(accessToken, expiresIn);
    }

    /**
     * 获取应用的管理员列表
     * @param suiteAccessToken
     * @param corpid
     * @param agentid
     * @return
     */
    public String getAdminList(String suiteAccessToken, String corpid, Integer agentid ) {
        String url = WxCpApiPathConsts.getURL(WxCpApiPathConsts.Tp.GET_ADMIN_LIST) + suiteAccessToken;
        Map<String, Object> params= new HashMap<>();
        params.put("auth_corpid", corpid);
        params.put("agentid", agentid);
        JSONObject jsonObject = restTemplate.postForObject(url, JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        JSONArray admin = jsonObject.getJSONArray("admin");
        //todo 后续处理
        return null;
    }

    /**
     * 获取访问用户身份
     * @param suiteAccessToken
     * @param code 通过成员授权获取到的code
     * @return
     */
    public String getUserInfo3rd(String suiteAccessToken, String code) {
        String url = String.format(WxCpApiPathConsts.getURL(GET_USER_INFO_3RD),suiteAccessToken, code);
        Map<String, Object> params= new HashMap<>();
        params.put("access_token", suiteAccessToken);
        params.put("code", code);
        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class);
        checkErrCode(jsonObject);
        //todo 用户处理
        return jsonObject.getString("CorpId");
    }

    public String getUserDetail3rd(String suiteAccessToken, String userTicket) {
        String url = WxCpApiPathConsts.getURL(GET_USER_DETAIL_3RD) + suiteAccessToken;
        Map<String, Object> params= new HashMap<>();
        params.put("user_ticket", userTicket);
        JSONObject jsonObject = restTemplate.postForObject(url, JSON.toJSONString(params), JSONObject.class);
        checkErrCode(jsonObject);
        //todo 用户处理
        return null;
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

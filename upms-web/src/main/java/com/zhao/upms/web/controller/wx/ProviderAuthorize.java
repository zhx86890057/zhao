package com.zhao.upms.web.controller.wx;

import com.zteng.invest.server.aes.AesException;
import io.swagger.annotations.Api;

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * *   企业授权应用   方式一：从服务商网站发起
 * *
 * *   @author   wison
 */
@Controller
@RequestMapping(value = "/qy/auth")
@Api(value = "/qy/auth")
public class ProviderAuthorize {
    @Autowired
    private SQywxApplicationService sqywxApplicationService;
    //   授权页网址
    public static final String INSTALL_URL = "https://open.work.weixin.qq.com/3rdapp/install?suite_id=";

    /**
     * *   一键授权功能,主动引入用户进入授权页后，通过用户点击调用此方法
     * *
     * *   @param   request
     * *   @param   suiteId
     * *            应用id
     * *   @throws   IOException
     * *   @throws   AesException
     * *   @throws   DocumentException
     */
    @RequestMapping(value = "/goAuthor")
    @ResponseBody
    public JsonResult goAuthor(HttpServletRequest request, String suiteId) throws IOException, AesException, DocumentException {
        if (StringUtils.isEmpty(suiteId)) {
            return new JsonResult(Message.M4003);
        }
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String redirectUri = baseUrl + "/qy/auth/authorCallback";
        String redirectUriEncode = URLEncoder.encode(redirectUri, "UTF-8");        //   Map<String,String>   stateMap   =   new   HashMap<String,   String>();
        //   stateMap.put("suiteId",   suiteId);
        //   查询第三方应用，获取预授权码
        SQywxApplication application = sqywxApplicationService.queryBySuiteId(suiteId);
        if (application == null || StringUtil.isEmpty(application.getPreAuthCode())) {
            return new JsonResult(Message.M3001, "suiteId:" + suiteId + "对应的第三方应用尚未初始化，请等待10分钟或联系服务商", null);
        }        //   获取预授权码，有效期10分钟
        String preAuthCode = application.getPreAuthCode();
        String url = INSTALL_URL + suiteId + "&pre_auth_code=" + preAuthCode + "&redirect_uri=" + redirectUriEncode + "&state=" + suiteId;
        return new JsonResult(Message.M2000, url);
    }

    /**
     * *   引导授权回调   根据临时授权码（10分钟有效），换取永久授权码
     * *
     * *   @param   request
     * *   @param   response
     * *   @throws   IOException
     * *   @throws   AesException
     * *   @throws   DocumentException
     */
    @RequestMapping(value = "/authorCallback")
    public void authorCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, AesException, DocumentException {
        String authCode = request.getParameter("auth_code");
        String expires_in = request.getParameter("expires_in");
        String state = request.getParameter("state");        //   //解析state
        //   JSONObject   js   =   JSONObject.parseObject(state);
        //   String   suiteId   =   js.getString("suiteId");
        String suiteId = state;        //   换取永久授权码
        EnterpriseAPI.getPermanentCodeAndAccessToken(suiteId, authCode);
    }

}

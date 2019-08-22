package com.zhao.upms.web.controller.wx;

package com.ffxl.hi.controller.qywx;import io.swagger.annotations.Api;import io.swagger.annotations.ApiOperation;import java.io.UnsupportedEncodingException;import java.util.List;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.PathVariable;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.ResponseBody;import org.springframework.web.servlet.ModelAndView;import com.ffxl.cloud.model.SQywxApplication;import com.ffxl.cloud.service.SQywxApplicationAuthorizerService;import com.ffxl.cloud.service.SQywxApplicationService;import com.ffxl.hi.controller.qywx.util.EnterpriseAPI;import com.ffxl.hi.controller.qywx.util.EnterpriseConst;import com.ffxl.platform.exception.BusinessException;import com.ffxl.platform.qywx.model.ApiUserDetailResponse;import com.ffxl.platform.qywx.model.ApiUserInfoResponse;import com.ffxl.platform.util.JsonResult;import com.ffxl.platform.util.Message;import com.ffxl.platform.util.StringUtil;/**
  * 用户在不告知第三方自己的帐号密码情况下，通过授权方式，让第三方服务可以获取自己的资源信息 网页授权登陆 第三方应用oauth2链接
  * 
  * @author wison
  */@Controller@RequestMapping(value = "/qy/oauth2")@Api(value = "/qy/oauth2")public class OAuth2Authorize {    private static final Logger logger = LoggerFactory.getLogger(OAuth2Authorize.class);    @Autowired
    private SQywxApplicationService sqywxApplicationService;    @Autowired
    private SQywxApplicationAuthorizerService sqywxApplicationAuthorizerService;    // oauth2地址
            public static final String OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=";    /**
      * 企业微信网页授权API
      * 
      * @param appId
      *            第三方应用id（即ww或wx开头的suite_id）。注意与企业的网页授权登录不同
      * @param pageView
      * @param scope
      *            应用授权作用域。
      * @param request
      * @param response
      * @return
      */
            @RequestMapping(value = "/getRedirectUrl")    @ResponseBody
    public JsonResult getRedirectUrl(String suiteId, String pageView, String scope, HttpServletRequest request, HttpServletResponse response) {        if (StringUtil.isEmpty(suiteId, pageView, scope)) {            throw new BusinessException(Message.M4003);
                }
                EnterpriseConst suiteConst = new EnterpriseConst("suite");
                suiteConst.setKey(suiteId);
                String suiteSecret = suiteConst.getValue();        if (StringUtil.isEmpty(suiteSecret)) {            throw new BusinessException(Message.M4004);
                }        // 第一步：引导用户进入授权页面,根据应用授权作用域，获取code
                String basePath = request.getScheme() + "://" + request.getServerName();
                String backUrl = basePath + "/third/qy/oauth2/redirect/" + suiteId;
                logger.info("------------------------回调地址:----" + backUrl);        // 微信授权地址
                String redirectUrl = oAuth2Url(suiteId, backUrl, scope, pageView);
                logger.info("------------------------授权地址:----" + redirectUrl);        return new JsonResult(true, redirectUrl);
            }    /**
      * 构造带员工身份信息的URL
      * 
      * @param appid
      *            第三方应用id（即ww或wx开头的suite_id）
      * @param redirect_uri
      *            授权后重定向的回调链接地址，请使用urlencode对链接进行处理
      * @param state
      *            重定向后会带上state參数，企业能够填写a-zA-Z0-9的參数值
      * @return
      */
            private String oAuth2Url(String suiteId, String redirect_uri, String scope, String state) {        try {
                    redirect_uri = java.net.URLEncoder.encode(redirect_uri, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String oauth2Url = OAUTH2_URL + suiteId + "&redirect_uri=" + redirect_uri + "&response_type=code" + "&scope=" + scope + "&state=" + state
                        + "#wechat_redirect";
                System.out.println("oauth2Url=" + oauth2Url);        return oauth2Url;
            }    /**
      * 微信回调地址
      * 
      * @param request
      * @return
      */
            @RequestMapping(value = "/redirect/{suiteId}")    @ApiOperation(value = "微信回调地址", httpMethod = "GET", hidden = true, notes = "微信回调地址")    public ModelAndView redirectDetail(@PathVariable String suiteId, HttpServletRequest request, HttpServletResponse response) {

                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
                response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");        if (StringUtil.isEmpty(suiteId)) {            throw new BusinessException(Message.M4003);
                }
                ModelAndView mv = new ModelAndView();        try {
                    String code = request.getParameter("code");            // state为跳转路径，微信不识别&符号，顾前端如有参数，请使用@代替，在此处做转换
                    // 页面路径必须在jsp目录下，后缀名可自定义
                    String state = request.getParameter("state");
                    logger.debug("------------------------state:----" + state);
                    state = state.replaceAll("@", "&");
                    logger.debug("------------------------state:----" + state);            // 用户授权
                    SQywxApplication application = sqywxApplicationService.queryBySuiteId(suiteId);            if (application == null) {                throw new BusinessException("suiteId:" + suiteId + "对应的第三方应用尚未初始化，请等待10分钟或联系服务商管理员");
                    }
                    String suiteAccessToken = application.getSuiteAccessToken();
                    logger.info("参数==================================");
                    logger.info("appid:" + suiteId);
                    logger.info("code:" + code);
                    logger.info("suiteAccessToken:" + suiteAccessToken);            // 第一个第三方应用
                    if (EnterpriseConst.SUITE_ID_1.equals(suiteId)) {                // 授权
                        ApiUserInfoResponse userInfo = EnterpriseAPI.getuserinfo3rd(suiteAccessToken, code);                if (userInfo != null && StringUtil.isEmpty(userInfo.getUserId()) && StringUtil.isEmpty(userInfo.getUserTicket())) {
                            mv.addObject("code", "5001");
                            mv.addObject("msg", "成员没有加入企业微信~");
                            mv.setViewName("qywx/error.jsp"); // 跳转等待页面，然后再跳回之前页面
                            return mv;
                        }
                        String corpId = userInfo.getCorpId(); // 授权方的企业id
                        ApiUserDetailResponse userDetail3rd = EnterpriseAPI.getUserDetail3rd(suiteAccessToken, userInfo.getUserTicket());                // 验证用户是否咨询师身份
                        boolean isConsole = false;
                        String accessToken = EnterpriseAPI.getCorpAccessToken(suiteAccessToken, suiteId, corpId);
                        ApiUserDetailResponse userDetail = EnterpriseAPI.getUserDepartment(accessToken, userInfo.getUserId());
                        List<Integer> departmentList = userDetail.getDepartmentList();
                        List<String> departmentNameList = userDetail.getDepartmentNameList();                if (departmentList.contains(EnterpriseConst.SUITE_ID_1_DEPARTMENT_CONSOLE)) {
                            isConsole = true;
                        }
                        mv.addObject("error", false);
                        mv.addObject("isConsole", isConsole);
                        mv.addObject("userId", userInfo.getUserId());
                        mv.addObject("corpId", corpId);
                        mv.addObject("departmentName", departmentNameList);
                        mv.addObject("name", userDetail3rd.getName());
                        mv.addObject("avatar", userDetail3rd.getAvatar());
                        mv.addObject("pageView", state);
                    } else if (EnterpriseConst.SUITE_ID_2.equals(suiteId)) {                // 授权
                        ApiUserInfoResponse userInfo = EnterpriseAPI.getuserinfo3rd(suiteAccessToken, code);                if (userInfo != null && StringUtil.isEmpty(userInfo.getUserId()) && StringUtil.isEmpty(userInfo.getUserTicket())) {
                            mv.addObject("code", "5001");
                            mv.addObject("msg", "成员没有加入企业微信~");
                            mv.setViewName("qywx/error.jsp"); // 跳转等待页面，然后再跳回之前页面
                            return mv;
                        }
                        String corpId = userInfo.getCorpId(); // 授权方的企业id
                        ApiUserDetailResponse userDetail3rd = EnterpriseAPI.getUserDetail3rd(suiteAccessToken, userInfo.getUserTicket());                // 验证用户是否咨询师身份
                        boolean isConsole = false;
                        String accessToken = EnterpriseAPI.getCorpAccessToken(suiteAccessToken, suiteId, corpId);
                        ApiUserDetailResponse userDetail = EnterpriseAPI.getUserDepartment(accessToken, userInfo.getUserId());
                        List<Integer> departmentList = userDetail.getDepartmentList();
                        List<String> departmentNameList = userDetail.getDepartmentNameList();                if (departmentList.contains(EnterpriseConst.SUITE_ID_2_DEPARTMENT_CONSOLE)) {
                            isConsole = true;
                        }
                        logger.info("返回用户======================" + userDetail);
                        logger.info("返回部门======================" + departmentNameList);
                        mv.addObject("error", false);
                        mv.addObject("isConsole", isConsole);
                        mv.addObject("userId", userInfo.getUserId());
                        mv.addObject("corpId", corpId);
                        mv.addObject("departmentName", departmentNameList);
                        mv.addObject("name", userDetail3rd.getName());
                        mv.addObject("avatar", userDetail3rd.getAvatar());
                        mv.addObject("pageView", state);
                    } else if (EnterpriseConst.SUITE_ID_3.equals(suiteId)) {                // 授权
                        ApiUserInfoResponse userInfo = EnterpriseAPI.getuserinfo3rd(suiteAccessToken, code);                if (userInfo != null && StringUtil.isEmpty(userInfo.getUserId()) && StringUtil.isEmpty(userInfo.getUserTicket())) {
                            mv.addObject("code", "5001");
                            mv.addObject("msg", "成员没有加入企业微信~");
                            mv.setViewName("qywx/error.jsp"); // 未加入企业微信
                            return mv;
                        }
                        String corpId = userInfo.getCorpId(); // 授权方的企业id
                        ApiUserDetailResponse userDetail3rd = EnterpriseAPI.getUserDetail3rd(suiteAccessToken, userInfo.getUserTicket());                // 验证用户是否咨询师身份
                        boolean isConsole = false;
                        String accessToken = EnterpriseAPI.getCorpAccessToken(suiteAccessToken, suiteId, corpId);
                        ApiUserDetailResponse userDetail = EnterpriseAPI.getUserDepartment(accessToken, userInfo.getUserId());
                        List<Integer> departmentList = userDetail.getDepartmentList();
                        List<String> departmentNameList = userDetail.getDepartmentNameList();                if (departmentList.contains(EnterpriseConst.SUITE_ID_3_DEPARTMENT_CONSOLE)) {
                            isConsole = true;
                        }
                        logger.info("返回用户======================" + userDetail);
                        logger.info("返回部门======================" + departmentNameList);
                        mv.addObject("error", false);
                        mv.addObject("isConsole", isConsole);
                        mv.addObject("userId", userInfo.getUserId());
                        mv.addObject("corpId", corpId);
                        mv.addObject("departmentName", departmentNameList);
                        mv.addObject("name", userDetail3rd.getName());
                        mv.addObject("avatar", userDetail3rd.getAvatar());
                        mv.addObject("pageView", state);
                    } else {
                        mv.addObject("error", true);
                    }
                    mv.setViewName("qywx/loading.jsp"); // 跳转等待页面，然后再跳回之前页面
                    return mv;
                } catch (BusinessException e) {
                    mv.addObject("code", "5000");
                    mv.addObject("msg", "出错了，点击返回首页");
                    mv.setViewName("qywx/error.jsp"); // 跳转等待页面，然后再跳回之前页面
                    return mv;
                }
            }    /**
      * 企业微信后台回调地址
      * 
      * @param request
      * @return
      */
            @RequestMapping(value = "/admin/redirect")    public ModelAndView adminRedirectDetail(String auth_code, HttpServletRequest request, HttpServletResponse response) {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
                response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");        if (StringUtil.isEmpty(auth_code)) {            throw new BusinessException(Message.M4003);
                }
                ModelAndView mv = new ModelAndView();
                logger.info("参数==================================");
                logger.info("code:" + auth_code);        try {
                    String providerAccessToken = EnterpriseAPI.getProviderToken(EnterpriseConst.SCORPID, EnterpriseConst.PROVIDERSECRET);
                    ApiUserDetailResponse userDetail = EnterpriseAPI.getLoginInfo(providerAccessToken, auth_code);
                    String url = "http://wxadmin.feifanxinli.com/admin/wechat_user/login?" + "wechatUserId=" + userDetail.getUserId() + "&userName="
                            + userDetail.getName() + "&headlImg=" + userDetail.getAvatar();
                    mv.addObject("error", false);
                    mv.addObject("url", url);
                    mv.setViewName("qywx/admin/wuxigongdian.jsp"); // 跳转等待页面，然后再跳回之前页面
                    return mv;
                } catch (Exception e) {
                    mv.addObject("error", true);
                    mv.setViewName("qywx/admin/wuxigongdian.jsp"); // 跳转等待页面，然后再跳回之前页面
                    return mv;
                }
            }
        }

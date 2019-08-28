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
        String suiteAccussToken = "8qibKlIP0fBCR1FvfoxLTrQuzGRXmzYQAyjdINfo1GR9kfIFmI16aUMqMEGS29-duhNhbDY7G7_4iuBz5BEmVuzYxOJ28QsNsdrMNNQOm2H5pTZnYJVvo1VzXi77K2io";
        return wxAPI.getUserInfo3rd(suiteAccussToken, code);
    }

    /**
     * 企业oauth2链接回调
     * @param code
     * @return
     */
    @GetMapping("/callbackCorp")
    public String callbackCorp(@RequestParam(name = "code") String code){
        String suiteAccussToken = "8qibKlIP0fBCR1FvfoxLTrQuzGRXmzYQAyjdINfo1GR9kfIFmI16aUMqMEGS29-duhNhbDY7G7_4iuBz5BEmVuzYxOJ28QsNsdrMNNQOm2H5pTZnYJVvo1VzXi77K2io";
        return wxAPI.getUserInfo3rd(suiteAccussToken, code);
    }


    /**
     * 微信回调地址
     *
     * @param request
     * @return
     */
//    @RequestMapping(value = "/redirect/{suiteId}")
//    @ApiOperation(value = "微信回调地址", httpMethod = "GET", hidden = true, notes = "微信回调地址")
//    static ModelAndView redirectDetail(@PathVariable String suiteId, HttpServletRequest request, HttpServletResponse response) {
//
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
//        response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
//        if (StringUtil.isEmpty(suiteId)) {
//            throw new BusinessException(Message.M4003);
//        }
//        ModelAndView mv = new ModelAndView();
//        try {
//            String code = request.getParameter("code");            // state为跳转路径，微信不识别&符号，顾前端如有参数，请使用@代替，在此处做转换
//            // 页面路径必须在jsp目录下，后缀名可自定义
//            String state = request.getParameter("state");
//            logger.debug("------------------------state:----" + state);
//            state = state.replaceAll("@", "&");
//            logger.debug("------------------------state:----" + state);            // 用户授权
//            SQywxApplication application = sqywxApplicationService.queryBySuiteId(suiteId);
//            if (application == null) {
//                throw new BusinessException("suiteId:" + suiteId + "对应的第三方应用尚未初始化，请等待10分钟或联系服务商管理员");
//            }
//            String suiteAccessToken = application.getSuiteAccessToken();
//            logger.info("参数==================================");
//            logger.info("appid:" + suiteId);
//            logger.info("code:" + code);
//            logger.info("suiteAccessToken:" + suiteAccessToken);            // 第一个第三方应用
//            if (EnterpriseConst.SUITE_ID_1.equals(suiteId)) {                // 授权
//                ApiUserInfoResponse userInfo = EnterpriseAPI.getuserinfo3rd(suiteAccessToken, code);
//                if (userInfo != null && StringUtil.isEmpty(userInfo.getUserId()) && StringUtil.isEmpty(userInfo.getUserTicket())) {
//                    mv.addObject("code", "5001");
//                    mv.addObject("msg", "成员没有加入企业微信~");
//                    mv.setViewName("qywx/error.jsp"); // 跳转等待页面，然后再跳回之前页面
//                    return mv;
//                }
//                String corpId = userInfo.getCorpId(); // 授权方的企业id
//                ApiUserDetailResponse userDetail3rd = EnterpriseAPI.getUserDetail3rd(suiteAccessToken, userInfo.getUserTicket());                // 验证用户是否咨询师身份
//                boolean isConsole = false;
//                String accessToken = EnterpriseAPI.getCorpAccessToken(suiteAccessToken, suiteId, corpId);
//                ApiUserDetailResponse userDetail = EnterpriseAPI.getUserDepartment(accessToken, userInfo.getUserId());
//                List<Integer> departmentList = userDetail.getDepartmentList();
//                List<String> departmentNameList = userDetail.getDepartmentNameList();
//                if (departmentList.contains(EnterpriseConst.SUITE_ID_1_DEPARTMENT_CONSOLE)) {
//                    isConsole = true;
//                }
//                mv.addObject("error", false);
//                mv.addObject("isConsole", isConsole);
//                mv.addObject("userId", userInfo.getUserId());
//                mv.addObject("corpId", corpId);
//                mv.addObject("departmentName", departmentNameList);
//                mv.addObject("name", userDetail3rd.getName());
//                mv.addObject("avatar", userDetail3rd.getAvatar());
//                mv.addObject("pageView", state);
//            } else if (EnterpriseConst.SUITE_ID_2.equals(suiteId)) {                // 授权
//                ApiUserInfoResponse userInfo = EnterpriseAPI.getuserinfo3rd(suiteAccessToken, code);
//                if (userInfo != null && StringUtil.isEmpty(userInfo.getUserId()) && StringUtil.isEmpty(userInfo.getUserTicket())) {
//                    mv.addObject("code", "5001");
//                    mv.addObject("msg", "成员没有加入企业微信~");
//                    mv.setViewName("qywx/error.jsp"); // 跳转等待页面，然后再跳回之前页面
//                    return mv;
//                }
//                String corpId = userInfo.getCorpId(); // 授权方的企业id
//                ApiUserDetailResponse userDetail3rd = EnterpriseAPI.getUserDetail3rd(suiteAccessToken, userInfo.getUserTicket());                // 验证用户是否咨询师身份
//                boolean isConsole = false;
//                String accessToken = EnterpriseAPI.getCorpAccessToken(suiteAccessToken, suiteId, corpId);
//                ApiUserDetailResponse userDetail = EnterpriseAPI.getUserDepartment(accessToken, userInfo.getUserId());
//                List<Integer> departmentList = userDetail.getDepartmentList();
//                List<String> departmentNameList = userDetail.getDepartmentNameList();
//                if (departmentList.contains(EnterpriseConst.SUITE_ID_2_DEPARTMENT_CONSOLE)) {
//                    isConsole = true;
//                }
//                logger.info("返回用户======================" + userDetail);
//                logger.info("返回部门======================" + departmentNameList);
//                mv.addObject("error", false);
//                mv.addObject("isConsole", isConsole);
//                mv.addObject("userId", userInfo.getUserId());
//                mv.addObject("corpId", corpId);
//                mv.addObject("departmentName", departmentNameList);
//                mv.addObject("name", userDetail3rd.getName());
//                mv.addObject("avatar", userDetail3rd.getAvatar());
//                mv.addObject("pageView", state);
//            } else if (EnterpriseConst.SUITE_ID_3.equals(suiteId)) {                // 授权
//                ApiUserInfoResponse userInfo = EnterpriseAPI.getuserinfo3rd(suiteAccessToken, code);
//                if (userInfo != null && StringUtil.isEmpty(userInfo.getUserId()) && StringUtil.isEmpty(userInfo.getUserTicket())) {
//                    mv.addObject("code", "5001");
//                    mv.addObject("msg", "成员没有加入企业微信~");
//                    mv.setViewName("qywx/error.jsp"); // 未加入企业微信
//                    return mv;
//                }
//                String corpId = userInfo.getCorpId(); // 授权方的企业id
//                ApiUserDetailResponse userDetail3rd = EnterpriseAPI.getUserDetail3rd(suiteAccessToken, userInfo.getUserTicket());                // 验证用户是否咨询师身份
//                boolean isConsole = false;
//                String accessToken = EnterpriseAPI.getCorpAccessToken(suiteAccessToken, suiteId, corpId);
//                ApiUserDetailResponse userDetail = EnterpriseAPI.getUserDepartment(accessToken, userInfo.getUserId());
//                List<Integer> departmentList = userDetail.getDepartmentList();
//                List<String> departmentNameList = userDetail.getDepartmentNameList();
//                if (departmentList.contains(EnterpriseConst.SUITE_ID_3_DEPARTMENT_CONSOLE)) {
//                    isConsole = true;
//                }
//                logger.info("返回用户======================" + userDetail);
//                logger.info("返回部门======================" + departmentNameList);
//                mv.addObject("error", false);
//                mv.addObject("isConsole", isConsole);
//                mv.addObject("userId", userInfo.getUserId());
//                mv.addObject("corpId", corpId);
//                mv.addObject("departmentName", departmentNameList);
//                mv.addObject("name", userDetail3rd.getName());
//                mv.addObject("avatar", userDetail3rd.getAvatar());
//                mv.addObject("pageView", state);
//            } else {
//                mv.addObject("error", true);
//            }
//            mv.setViewName("qywx/loading.jsp"); // 跳转等待页面，然后再跳回之前页面
//            return mv;
//        } catch (BusinessException e) {
//            mv.addObject("code", "5000");
//            mv.addObject("msg", "出错了，点击返回首页");
//            mv.setViewName("qywx/error.jsp"); // 跳转等待页面，然后再跳回之前页面
//            return mv;
//        }
//    }

    /**
     * 企业微信后台回调地址
     *
     * @param request
     * @return
     */
//    @RequestMapping(value = "/admin/redirect")
//    static ModelAndView adminRedirectDetail(String auth_code, HttpServletRequest request, HttpServletResponse response) {
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
//        response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
//        if (StringUtil.isEmpty(auth_code)) {
//            throw new BusinessException(Message.M4003);
//        }
//        ModelAndView mv = new ModelAndView();
//        logger.info("参数==================================");
//        logger.info("code:" + auth_code);
//        try {
//            String providerAccessToken = EnterpriseAPI.getProviderToken(EnterpriseConst.SCORPID, EnterpriseConst.PROVIDERSECRET);
//            ApiUserDetailResponse userDetail = EnterpriseAPI.getLoginInfo(providerAccessToken, auth_code);
//            String url = "http://wxadmin.feifanxinli.com/admin/wechat_user/login?" + "wechatUserId=" + userDetail.getUserId() + "&userName="
//                    + userDetail.getName() + "&headlImg=" + userDetail.getAvatar();
//            mv.addObject("error", false);
//            mv.addObject("url", url);
//            mv.setViewName("qywx/admin/wuxigongdian.jsp"); // 跳转等待页面，然后再跳回之前页面
//            return mv;
//        } catch (Exception e) {
//            mv.addObject("error", true);
//            mv.setViewName("qywx/admin/wuxigongdian.jsp"); // 跳转等待页面，然后再跳回之前页面
//            return mv;
//        }
//    }
}

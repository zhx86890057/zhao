//package com.zhao.upms.web.controller.wx;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@Controller
//@RequestMapping(value = "/qy/jssdk")
//@Api(value = "/qy/jssdk")
//static class JSSDK {
//    private static final Logger logger = LoggerFactory.getLogger(JSSDK.class);
//    @Autowired
//    private SQywxApplicationService sqywxApplicationService;
//
//    /**
//     * 企业微信使用jssdk参数
//     *
//     * @return
//     * @throws IOException
//     */
//    @RequestMapping(value = "/config/{suiteId}/{authCorpId}")
//    @ResponseBody
//    @ApiOperation(value = "公众号回调地址", httpMethod = "GET", hidden = true)
//    static JsonResult jssdkconfig(@PathVariable String suiteId, @PathVariable String authCorpId, String requestUrl, HttpServletResponse response) {
//        if (StringUtils.isEmpty(suiteId, authCorpId, requestUrl)) {
//            return new JsonResult(Message.M4003);
//        }        //  根据suiteId查询第三方信息
//        SQywxApplication application = sqywxApplicationService.queryBySuiteId(suiteId);
//        if (application == null || StringUtil.isEmpty(application.getSuiteAccessToken())) {
//            return new JsonResult(Message.M3001, "suiteId:" + suiteId + "对应的第三方应用尚未初始化，请等待10分钟或联系服务商管理严", null);
//        }
//        String accessToken = EnterpriseAPI.getCorpAccessToken(application.getSuiteAccessToken(), suiteId, authCorpId);
//        Map<String, String> tickMap = EnterpriseAPI.getJsTicket(accessToken, suiteId, authCorpId);
//        String corpTicket = tickMap.get("corpTicket");
//        String ticket = tickMap.get("ticket");
//        String agentId = tickMap.get("agentId");        //  企业js-sdk签名
//        logger.info("requestUrl：" + requestUrl);
//        logger.info("suiteId：" + suiteId);
//        logger.info("authCorpId：" + authCorpId);
//        logger.info("corpTicket：" + corpTicket);
//        Map<String, Object> corpObjMap = EnterpriseAPI.getWxConfig(requestUrl, suiteId, authCorpId, corpTicket);
//        logger.info("corpSignature：" + corpObjMap.get("signature"));        //  js-sdk签名
//        logger.info("requestUrl：" + requestUrl);
//        logger.info("suiteId：" + suiteId);
//        logger.info("authCorpId：" + authCorpId);
//        logger.info("ticket：" + ticket);
//        logger.info("agentId：" + agentId);
//        Map<String, Object> objMap = EnterpriseAPI.getWxConfig(requestUrl, suiteId, authCorpId, ticket);
//        objMap.put("agentId", agentId);        //  查询企业的
//        logger.info("signature：" + objMap.get("signature"));
//        Map<String, Object> restObj = new HashMap<String, Object>();
//        restObj.put("config", corpObjMap);  //  企业
//        restObj.put("agentConfig", objMap);  //  应用
//        return new JsonResult(true, restObj);
//    }
//}

//package com.zhao.upms.web.controller.wx;
//
//import com.zteng.invest.server.aes.AesException;
//import io.swagger.annotations.Api;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.util.Calendar;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang3.StringUtils;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
///**
// * 企业微信开放了消息发送接口，企业可以使用这些接口让自定义应用与企业微信后台或用户间进行双向通信。
// *
// * @author wison
// */
//@Controller
//@RequestMapping(value = "/qy/message")
//@Api(value = "/qy/message")
//public class MessagePush extends BaseUtil {
//    private static final Logger logger = LoggerFactory.getLogger(MessagePush.class);
//
//    /**
//     * 授权公众号的回调地址 处理消息等用户操作时，请务必使用appid进行匹配
//     *
//     * @param appid
//     * @param request
//     * @param response
//     * @throws IOException
//     * @throws AesException
//     * @throws DocumentException
//     */
//    @RequestMapping(value = "{corpid}/callback")
//    public void acceptMessageAndEvent(@PathVariable String corpid, HttpServletRequest request, HttpServletResponse response) throws IOException,
//            DocumentException, AesException {
//        String nonce = request.getParameter("nonce");
//        String timestamp = request.getParameter("timestamp");
//        String msgSignature = request.getParameter("msg_signature");
//        String echostr = request.getParameter("echostr");
//        logger.info("-----------------------corpid:" + corpid);
//        logger.info("-----------------------nonce:" + nonce);
//        logger.info("-----------------------timestamp:" + timestamp);
//        logger.info("-----------------------msg_signature:" + msgSignature); // 签名串
//        logger.info("-----------------------echostr:" + echostr);// 随机串
//        String sEchoStr; // url验证时需要返回的明文
//        if ( StringUtils.isEmpty(msgSignature)) return;// 微信推送给第三方开放平台的消息一定是加过密的，无消息加密无法解密消息
//        if ( StringUtils.isEmpty(echostr)) {            // 消息处理
//            StringBuilder sb = new StringBuilder();
//            BufferedReader in = request.getReader();
//            String line;
//            while ((line = in.readLine()) != null) {
//                sb.append(line);
//            }
//            in.close();
//            String xml = sb.toString();
//            logger.info("接收到的xml信息----------" + xml);
//            checkWeixinAllNetworkCheck(request, response, corpid, xml);
//        } else {            // 校验，此处的receiveid为企业的corpid
//            WXBizMsgCrypt wxcorp = new WXBizMsgCrypt(EnterpriseConst.STOKEN, EnterpriseConst.SENCODINGAESKEY, corpid);
//            sEchoStr = wxcorp.VerifyURL(msgSignature, timestamp, nonce, echostr);
//            logger.info("-----------------------URL验证成功，返回解析后的的echostr:" + sEchoStr);
//            output(response, sEchoStr); // 输出响应的内容。
//        }
//
//    }
//
//    /**
//     * 解密消息
//     *
//     * @param appid
//     * @param request
//     * @param response
//     * @param xml
//     * @throws DocumentException
//     * @throws IOException
//     * @throws AesException
//     */
//    public void checkWeixinAllNetworkCheck(HttpServletRequest request, HttpServletResponse response, String corpid, String xml)
//            throws DocumentException, IOException {
//        Document doc = DocumentHelper.parseText(xml);
//        Element rootElt = doc.getRootElement();
//        String toUserName = rootElt.elementText("ToUserName"); // 企业微信的CorpID，当为第三方套件回调事件时，CorpID的内容为suiteid
//        String agentID = rootElt.elementText("AgentID"); // 接收的应用id，可在应用的设置页面获取
//        String encrypt = rootElt.elementText("Encrypt"); // 消息结构体加密后的字符串
//
//        try {
//            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(EnterpriseConst.STOKEN, EnterpriseConst.SENCODINGAESKEY, corpid);            // 解析出url上的参数值如下：
//            String sVerifyNonce = request.getParameter("nonce");
//            String sVerifyTimeStamp = request.getParameter("timestamp");
//            String sVerifyMsgSig = request.getParameter("msg_signature");            // 检验消息的真实性，并且获取解密后的明文.
//            String sEncryptXml = wxcpt.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, xml);
//
//            parsingMsg(request, response, sEncryptXml);
//        } catch (Exception e) {            // 验证URL失败，错误原因请查看异常
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 对解密后的xml信息进行处理
//     *
//     * @param xml
//     */
//    void parsingMsg(HttpServletRequest request, HttpServletResponse response, String sEncryptXml) throws DocumentException, IOException {
//        Document doc = DocumentHelper.parseText(sEncryptXml);
//        Element rootElt = doc.getRootElement();
//        String msgType = rootElt.elementText("MsgType");
//        String toUserName = rootElt.elementText("ToUserName");
//        String fromUserName = rootElt.elementText("FromUserName");
//
//        logger.info("---消息类型msgType：" + msgType + "-----------------企业微信CorpID:" + toUserName + "-----------------成员UserID:" + fromUserName);
//        if ("event".equals(msgType)) {
//            String event = rootElt.elementText("Event");
//            logger.info("开始解析事件消息--------,事件类型：" + event);            // replyEventMessage(request, response, event, toUserName, fromUserName);
//        } else if ("text".equals(msgType)) {
//            logger.info("开始解析文本消息--------");
//            String content = rootElt.elementText("Content");
//            processTextMessage(request, response, content, toUserName, fromUserName);
//        }
//    }
//
//    /**
//     * 事件消息
//     *
//     * @param request
//     * @param response
//     * @param event
//     * @param toUserName
//     * @param fromUserName
//     * @param appid
//     * @throws DocumentException
//     * @throws IOException
//     */
//    public void replyEventMessage(HttpServletRequest request, HttpServletResponse response, String event, String toUserName, String fromUserName)
//            throws DocumentException, IOException {
//        switch (event) {
//            case "":
//                break;
//            default:
//                break;
//        }
//
//        String content = event + "from_callback";
//        logger.info("---全网发布接入检测------step.4-------事件回复消息  content=" + content + "   toUserName=" + toUserName + "   fromUserName=" + fromUserName);
//        replyTextMessage(request, response, content, toUserName, fromUserName);
//    }
//
//    /**
//     * 文本消息
//     *
//     * @param request
//     * @param response
//     * @param content
//     * @param toUserName
//     * @param fromUserName
//     * @param appid
//     * @throws IOException
//     * @throws DocumentException
//     */
//    public void processTextMessage(HttpServletRequest request, HttpServletResponse response, String content, String toUserName, String fromUserName)
//            throws IOException, DocumentException {
//        String reContent = content + "from_callback";
//        logger.info("---全网发布接入检测------step.4-------文本回复消息  content=" + content + "   toUserName=" + toUserName + "   fromUserName=" + fromUserName);
//        replyTextMessage(request, response, content, toUserName, fromUserName);
//    }
//
//    /**
//     * 回复微信服务器"文本消息"
//     *
//     * @param request
//     * @param response
//     * @param content
//     * @param toUserName
//     * @param fromUserName
//     * @throws DocumentException
//     * @throws IOException
//     */
//    public void replyTextMessage(HttpServletRequest request, HttpServletResponse response, String content, String toUserName, String fromUserName)
//            throws DocumentException, IOException {
//        Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;
//        StringBuffer sb = new StringBuffer();
//        sb.append("<xml>");
//        sb.append("<ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>");
//        sb.append("<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>");
//        sb.append("<CreateTime>" + createTime + "</CreateTime>");
//        sb.append("<MsgType><![CDATA[text]]></MsgType>");
//        sb.append("<Content><![CDATA[" + content + "]]></Content>");
//        sb.append("</xml>");
//        String replyMsg = sb.toString();
//
//        String returnvaleue = "";
//        try {            // 此处的receiveid 随便定义均可通过加密算法，联系企业微信未得到合理解释，暂时按照文档，此处参数使用企业对应的corpid
//            logger.info("===================>>>企业coidId:" + toUserName);
//            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(EnterpriseConst.STOKEN, EnterpriseConst.SENCODINGAESKEY, toUserName);
//            returnvaleue = wxcpt.EncryptMsg(replyMsg, createTime.toString(), "easemob");
//        } catch (AesException e) {
//            e.printStackTrace();
//        }
//        output(response, returnvaleue);
//    }
//
//}

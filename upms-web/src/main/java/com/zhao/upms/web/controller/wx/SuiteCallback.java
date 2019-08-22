package com.zhao.upms.web.controller.wx;

import com.zteng.invest.server.aes.AesException;
import com.zteng.invest.server.aes.WXBizMsgCrypt;
import io.swagger.annotations.Api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ————————————————
 * * 推送更新token
 * *
 * * @author wison
 */
@Controller
@RequestMapping(value = "/qy/suite")
@Api(value = "/qy/suite")
public class SuiteCallback {
    private static final Logger logger = LoggerFactory.getLogger(SuiteCallback.class);
    @Autowired
    private SQywxApplicationService sqywxApplicationService;
    @Autowired
    private SQywxApplicationAuthorizerService sqywxApplicationAuthorizerService;

    /**
     * 指令接收 企业微信服务器会定时（每十分钟）推送ticket。ticket会实时变更，并用于后续接口的调用。
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping(value = "/directive/receive")
    public void acceptAuthorizeEvent(String suiteid, HttpServletRequest request, HttpServletResponse response) throws IOException, AesException,
            DocumentException {
        logger.debug("企业微信服务器开始推送suite_ticket---------10分钟一次-----------");
        logger.debug("获取到的第三方应用suiteid:" + suiteid);
        processAuthorizeEvent(suiteid, request, response);
    }

    /**
     * 服务商处理指令回调，解析suite_ticket数据
     *
     * @param request
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    public void processAuthorizeEvent(String suitedid, HttpServletRequest request, HttpServletResponse response) throws IOException,
            DocumentException, AesException {        // 第三方事件回调
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(EnterpriseConst.STOKEN, EnterpriseConst.SENCODINGAESKEY, suitedid);        // 解析出url上的参数值如下：
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String msgSignature = request.getParameter("msg_signature");
        String echostr = request.getParameter("echostr");
        String sEchoStr; // url验证时需要返回的明文
        if (StringUtils.isEmpty(msgSignature)) return;        // 回调
        if (StringUtils.isEmpty(echostr)) {
            StringBuilder sb = new StringBuilder();
            BufferedReader in = request.getReader();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            String xml = sb.toString();
            logger.debug("-----------------------服务商接收到的原始 Xml=" + xml);
            String exml = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, xml);
            logger.debug("-----------------------服务商接收到的xml解密后:" + exml);
            processAuthorizationEvent(request, exml);
            logger.debug("-----------------------解析成功，返回success");
            output(response, "success"); // 输出响应的内容。
        } else {            // 校验，此处的receiveid为企业的corpid
            WXBizMsgCrypt wxcorp = new WXBizMsgCrypt(EnterpriseConst.STOKEN, EnterpriseConst.SENCODINGAESKEY, EnterpriseConst.SCORPID);
            sEchoStr = wxcorp.VerifyURL(msgSignature, timestamp, nonce, echostr);
            logger.debug("-----------------------URL验证成功，返回解析后的的echostr:" + sEchoStr);
            output(response, "success"); // 输出响应的内容。
        }
    }

    /**
     * 对解密后的xml信息进行处理
     *
     * @param xml
     */
    void processAuthorizationEvent(HttpServletRequest request, String echoXml) {
        Document doc;
        try {
            doc = DocumentHelper.parseText(echoXml);
            Element rootElt = doc.getRootElement();            // 消息类型
            String infoType = rootElt.elementText("InfoType");
            String suiteId = rootElt.elementText("SuiteId");// 第三方应用的SuiteId
            switch (EnumType.InfoType.valueOf(infoType)) {            // 授权成功，从企业微信应用市场发起授权时，企业微信后台会推送授权成功通知。
                // 从第三方服务商网站发起的应用授权流程，由于授权完成时会跳转第三方服务商管理后台，因此不会通过此接口向第三方服务商推送授权成功通知。
                case create_auth:
                    String authCode = rootElt.elementText("AuthCode");// 授权的auth_code,最长为512字节。用于获取企业的永久授权码。5分钟内有效
                    logger.debug("》》》》》》》》》》授权码AuthCode：" + authCode);                // 换取企业永久授权码
                    EnterpriseAPI.getPermanentCodeAndAccessToken(suiteId, authCode);
                    break;            // 变更授权，服务商接收到变更通知之后，需自行调用获取企业授权信息进行授权内容变更比对。
                case change_auth:
                    String changeAuthCorpid = rootElt.elementText("AuthCorpId");// 授权方的corpid
                    // 获取并更新本地授权的企业信息
                    EnterpriseAPI.getAuthInfo(suiteId, changeAuthCorpid);
                    break;            // 取消授权，当授权方（即授权企业）在企业微信管理端的授权管理中，取消了对应用的授权托管后，企业微信后台会推送取消授权通知。
                case cancel_auth:                // TODO 删除企业授权信息
                    String cancelAuthCorpid = rootElt.elementText("AuthCorpId");// 授权方的corpid
                    sqywxApplicationAuthorizerService.deleteBySuiteAndCorpId(suiteId, cancelAuthCorpid);
                    break;            // 企业微信服务器会定时（每十分钟）推送ticket。ticket会实时变更，并用于后续接口的调用。
                case suite_ticket:
                    String suiteTicket = rootElt.elementText("SuiteTicket");
                    String timeStamp = rootElt.elementText("TimeStamp"); // 时间戳-秒
                    logger.debug("》》》》》》》》》》》》》》》》》》》》》》》》TimeStamp时间戳=========================" + timeStamp);                // 存储ticket
                    logger.debug("推送SuiteTicket协议-----------suiteTicket = " + suiteTicket);
                    EnterpriseConst suiteConst = new EnterpriseConst("suite");
                    suiteConst.setKey(suiteId);
                    String suiteSecret = suiteConst.getValue();
                    SQywxApplication entity = sqywxApplicationService.getQYWeixinApplication(suiteId, suiteSecret);
                    entity.setSuiteTicket(suiteTicket);
                    logger.debug("》》》》》》》》》》》》》》》》》》》》》》》》TimeStamp时间戳(毫秒)=========================" + Long.parseLong(timeStamp) * 1000);
                    Date date = new Date(Long.parseLong(timeStamp) * 1000);
                    logger.debug("》》》》》》》》》》》》》》》》》》》》》》》》TimeStamp时间戳=========================" + DateUtil.formatStandardDatetime(date));
                    Date ticketDate = DateUtil.parseDate(DateUtil.formatStandardDatetime(date));
                    entity.setTicketTime(ticketDate);
                    int ret = sqywxApplicationService.updateByPrimaryKeySelective(entity);                // 获取第三方应用凭证，有效期2小时
                    ApiSuiteTokenRequest apiSuiteToken = new ApiSuiteTokenRequest();
                    apiSuiteToken.setSuite_id(suiteId);
                    apiSuiteToken.setSuite_secret(suiteSecret);                // 授权事件接收会每隔10分钟检验一下ticket的有效性，从而保证了此处的ticket是长期有效的
                    apiSuiteToken.setSuite_ticket(suiteTicket);                // 验证token有效性(2小时)
                    String suiteAccessToken = EnterpriseAPI.getSuiteAccessToken(apiSuiteToken);                // 验证预授权码有效性(10分钟)
                    EnterpriseAPI.getPreAuthCode(suiteId, suiteAccessToken);
                    break;            // 变更通知，根据ChangeType区分消息类型
                case change_contact:                // TODO 更新令牌等
                    break;
                default:
                    break;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}

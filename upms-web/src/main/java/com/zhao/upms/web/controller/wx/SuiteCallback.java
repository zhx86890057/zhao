package com.zhao.upms.web.controller.wx;

import com.zhao.upms.web.aes.AesException;
import com.zhao.upms.web.aes.WXBizMsgCrypt;
import com.zhao.upms.web.constant.WxAppConfigs;
import com.zhao.upms.web.constant.WxCpApiPathConsts;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import static com.zhao.upms.web.constant.WxEnumType.*;

/**
 * ————————————————
 * * 推送更新token
 * *
 * * @author wison
 */
@RestController
@RequestMapping(value = "/wx/suite/receive")
@Slf4j
public class SuiteCallback {

    /**
     * Get请求验证URL有效性
     * 服务商处理指令回调，解析suite_ticket数据
     *
     * @param request
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping("/{suiteId}")
    public String check(@PathVariable String suiteId, HttpServletRequest request, HttpServletResponse response) throws IOException,
            DocumentException, AesException {        // 第三方事件回调
        WxAppConfigs.AppConfig appConfig = WxAppConfigs.getAppConfig(suiteId);
        if(appConfig == null){
            return "应用suiteid不存在";
        }
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String msgSignature = request.getParameter("msg_signature");
        String echostr = request.getParameter("echostr");
        String sEchoStr;// url验证时需要返回的明文
        if (StringUtils.isEmpty(echostr)) {
            StringBuilder sb = new StringBuilder();
            BufferedReader in = request.getReader();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            String xml = sb.toString();
            log.info("服务商接收到的原始 Xml={}", xml);
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(appConfig.getToken(), appConfig.getEncodingAESKey(), appConfig.getSuiteID());
            String exml = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, xml);
            log.info("服务商接收到的xml解密后:{}", exml);
            return "success";
        } else {
            // 校验url，此处的receiveid为企业的corpid
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(appConfig.getToken(), appConfig.getEncodingAESKey(), WxAppConfigs.corpId);
            sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
            log.info("URL验证成功，返回解析后的的echostr:{}", sEchoStr);
            return sEchoStr;
        }
    }

    /**
     * 对解密后的xml信息进行处理
     *
     * @param xml
     */
    public void processAuthorizationEvent(HttpServletRequest request, String xml) {
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();            // 消息类型
            String infoType = rootElt.elementText("InfoType");
            String suiteId = rootElt.elementText("SuiteId");// 第三方应用的SuiteId
            switch (infoType) {
                // 授权成功，从企业微信应用市场发起授权时，企业微信后台会推送授权成功通知。
                // 从第三方服务商网站发起的应用授权流程，由于授权完成时会跳转第三方服务商管理后台，因此不会通过此接口向第三方服务商推送授权成功通知。
                case create_auth:
                    String authCode = rootElt.elementText("AuthCode");// 授权的auth_code,最长为512字节。用于获取企业的永久授权码。5分钟内有效
                    log.info("授权码AuthCode：{}", authCode);                // 换取企业永久授权码
//                    EnterpriseAPI.getPermanentCodeAndAccessToken(suiteId, authCode);
                    break;
                // 变更授权，服务商接收到变更通知之后，需自行调用获取企业授权信息进行授权内容变更比对。
                case change_auth:
                    String changeAuthCorpid = rootElt.elementText("AuthCorpId");// 授权方的corpid
                    // 获取并更新本地授权的企业信息
//                    EnterpriseAPI.getAuthInfo(suiteId, changeAuthCorpid);
                    break;
                // 取消授权，当授权方（即授权企业）在企业微信管理端的授权管理中，取消了对应用的授权托管后，企业微信后台会推送取消授权通知。
                case cancel_auth:                // TODO 删除企业授权信息
                    String cancelAuthCorpid = rootElt.elementText("AuthCorpId");// 授权方的corpid
//                    sqywxApplicationAuthorizerService.deleteBySuiteAndCorpId(suiteId, cancelAuthCorpid);
                    break;
                // 企业微信服务器会定时（每十分钟）推送ticket。ticket会实时变更，并用于后续接口的调用。
                case suite_ticket:
                    String suiteTicket = rootElt.elementText("SuiteTicket");
                    String timeStamp = rootElt.elementText("TimeStamp"); // 时间戳-秒
                    log.info("推送SuiteTicket,suiteTicket = {}", suiteTicket);
                    EnterpriseConst suiteConst = new EnterpriseConst("suite");
                    suiteConst.setKey(suiteId);
                    String suiteSecret = suiteConst.getValue();
                    SQywxApplication entity = sqywxApplicationService.getQYWeixinApplication(suiteId, suiteSecret);
                    entity.setSuiteTicket(suiteTicket);
                    logger.info("》》》》》》》》》》》》》》》》》》》》》》》》TimeStamp时间戳(毫秒)=========================" + Long.parseLong(timeStamp) * 1000);
                    Date date = new Date(Long.parseLong(timeStamp) * 1000);
                    logger.info("》》》》》》》》》》》》》》》》》》》》》》》》TimeStamp时间戳=========================" + DateUtil.formatStandardDatetime(date));
                    Date ticketDate = DateUtil.parseDate(DateUtil.formatStandardDatetime(date));
                    entity.setTicketTime(ticketDate);
                    int ret = sqywxApplicationService.updateByPrimaryKeySelective(entity);                // 获取第三方应用凭证，有效期2小时
                    ApiSuiteTokenRequest apiSuiteToken = new ApiSuiteTokenRequest();
                    apiSuiteToken.setSuite_id(suiteId);
                    apiSuiteToken.setSuite_secret(suiteSecret);                // 授权事件接收会每隔10分钟检验一下ticket的有效性，从而保证了此处的ticket是长期有效的
                    apiSuiteToken.setSuite_ticket(suiteTicket);                // 验证token有效性(2小时)
                    String suiteAccessToken = EnterpriseAPI.getSuiteAccessToken(apiSuiteToken);                // 验证预授权码有效性(10分钟)
                    EnterpriseAPI.getPreAuthCode(suiteId, suiteAccessToken);
                    break;
                // 变更通知，根据ChangeType区分消息类型
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

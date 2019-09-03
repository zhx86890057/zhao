package com.zhao.upms.web.controller.wx;

import com.zhao.upms.web.aes.AesException;
import com.zhao.upms.web.aes.WXBizMsgCrypt;
import com.zhao.upms.web.constant.WxAppConfigs;
import com.zhao.upms.web.service.impl.WxAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    @Autowired
    private WxAPI wxAPI;

    @GetMapping("/{suiteId}")
    public String checkURL(@PathVariable String suiteId,
                           @RequestParam(name = "msg_signature", required = false) String signature,
                           @RequestParam(name = "timestamp", required = false) String timestamp,
                           @RequestParam(name = "nonce", required = false) String nonce,
                           @RequestParam(name = "echostr", required = false) String echostr) throws AesException {
       log.info("接收到来自微信服务器的验证消息：signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]",
                signature, timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            return "参数错误";
        }
        WxAppConfigs.AppConfig appConfig = WxAppConfigs.getAppConfig(suiteId);
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(appConfig.getToken(), appConfig.getEncodingAESKey(), WxAppConfigs.corpId);
        if (appConfig == null) {
            log.info("未找到对应suiteId={}的配置，请核实！", suiteId);
            return String.format("未找到对应suiteId=[%d]的配置，请核实！", suiteId);
        }
        String sEchoStr = wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
        log.info("URL验证成功，返回解析后的的echostr:{}", sEchoStr);
        return sEchoStr;
    }

    /**
     * 服务商处理指令回调
     *
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     */
    @RequestMapping("/{suiteId}")
    public String callbackHandler(@PathVariable String suiteId, @RequestBody String requestBody,
                        @RequestParam("msg_signature") String signature,
                        @RequestParam("timestamp") String timestamp,
                        @RequestParam("nonce") String nonce) throws AesException {
        this.log.info("接收微信请求：[signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[{}] ",
                signature, timestamp, nonce, requestBody);
        WxAppConfigs.AppConfig appConfig = WxAppConfigs.getAppConfig(suiteId);
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(appConfig.getToken(), appConfig.getEncodingAESKey(), appConfig.getSuiteID());
        String xml = wxcpt.DecryptMsg(signature, timestamp, nonce, requestBody);
        log.info("服务商接收到的xml解密后:{}", xml);
        processEvent(xml, appConfig);
        return "success";
    }

    /**
     * 对解密后的xml信息进行处理
     *
     * @param xml
     */
    public void processEvent(String xml, WxAppConfigs.AppConfig appConfig) {
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();            // 消息类型
            String infoType = rootElt.elementText("InfoType");
            String suiteId = rootElt.elementText("SuiteId");// 第三方应用的SuiteId
            switch (infoType) {
                // 授权成功，从企业微信应用市场发起授权时，企业微信后台会推送授权成功通知。
                // 从第三方服务商网站发起的应用授权流程，由于授权完成时会跳转第三方服务商管理后台，因此不会通过此接口向第三方服务商推送授权成功通知。
                case "create_auth":
                    String authCode = rootElt.elementText("AuthCode");// 授权的auth_code,最长为512字节。用于获取企业的永久授权码。5分钟内有效
                    log.info("授权码AuthCode：{}", authCode);                // 换取企业永久授权码
//                    EnterpriseAPI.getPermanentCodeAndAccessToken(suiteId, authCode);
                    break;
                // 变更授权，服务商接收到变更通知之后，需自行调用获取企业授权信息进行授权内容变更比对。
                case "change_auth":
                    String changeAuthCorpid = rootElt.elementText("AuthCorpId");// 授权方的corpid
                    // 获取并更新本地授权的企业信息
//                    EnterpriseAPI.getAuthInfo(suiteId, changeAuthCorpid);
                    break;
                // 取消授权，当授权方（即授权企业）在企业微信管理端的授权管理中，取消了对应用的授权托管后，企业微信后台会推送取消授权通知。
                case "cancel_auth":
                    // TODO 删除企业授权信息
                    String cancelAuthCorpid = rootElt.elementText("AuthCorpId");// 授权方的corpid
//                    sqywxApplicationAuthorizerService.deleteBySuiteAndCorpId(suiteId, cancelAuthCorpid);
                    break;
                // 企业微信服务器会定时（每十分钟）推送ticket。ticket会实时变更，并用于后续接口的调用。
                case "suite_ticket":
                    String suiteTicket = rootElt.elementText("SuiteTicket");
                    String timeStamp = rootElt.elementText("TimeStamp"); // 时间戳-秒
                    log.info("推送SuiteTicket,suiteTicket = {}", suiteTicket);
                    appConfig.setSuiteTicket(suiteTicket);
                    //判断suiteAccessToken是否过期
//                    if(appConfig.getExpiresTime() < System.currentTimeMillis()){
                        String suiteAccessToken = wxAPI.getSuiteAccessToken(appConfig);
                        String preAuthCode = wxAPI.getPreAuthCode(suiteAccessToken);
                        log.info("suiteId = {}, suiteAccessToken = {}, preAuthCode ={}", suiteId, suiteAccessToken,
                                preAuthCode);
//                    }
                    break;
                // 变更通知，根据ChangeType区分消息类型
                case "change_contact":
                    // TODO 更新令牌等
                    break;
                default:
                    break;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}

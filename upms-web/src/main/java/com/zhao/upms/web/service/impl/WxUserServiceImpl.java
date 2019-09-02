package com.zhao.upms.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhao.dao.domain.UmsMember;
import com.zhao.upms.web.constant.WxCpApiPathConsts;
import com.zhao.upms.web.constant.WxCpErrorMsgEnum;
import com.zhao.upms.web.wxBean.WxCpUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WxUserServiceImpl {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 创建成员
     * @param wxCpUser
     * @return
     */
    public void create(WxCpUser wxCpUser, String accessToken) {
        String url = WxCpApiPathConsts.User.USER_CREATE + accessToken;
        JSONObject jsonObject = restTemplate.postForObject(WxCpApiPathConsts.getURL(url), JSON.toJSONString(wxCpUser),
                JSONObject.class);
        checkErrCode(jsonObject);
    }

    /**
     * 读取成员
     * @param userid
     * @return
     */
    public WxCpUser getById(String userid, String accessToken){
        String url = String.format(WxCpApiPathConsts.User.USER_GET, accessToken);
        WxCpUser wxCpUser = restTemplate.getForObject(WxCpApiPathConsts.getURL(url), WxCpUser.class);
        return wxCpUser;
    }

    public void update(WxCpUser wxCpUser,String accessToken){
        String url = WxCpApiPathConsts.User.USER_UPDATE + accessToken;
        JSONObject jsonObject = restTemplate.postForObject(WxCpApiPathConsts.getURL(url), JSONObject.toJSONString(wxCpUser), JSONObject.class);
        checkErrCode(jsonObject);
    }

    public void delete(String userid, String accessToken){
        String url = String.format(WxCpApiPathConsts.User.USER_DELETE, accessToken, userid);
        JSONObject forObject = restTemplate.getForObject(WxCpApiPathConsts.getURL(url), JSONObject.class);
        checkErrCode(forObject);
    }

    public void checkErrCode(JSONObject jsonObject){
        int errcode = jsonObject.getIntValue("errcode");
        if(errcode != WxCpErrorMsgEnum.CODE_0.getCode()){
            String msgByCode = WxCpErrorMsgEnum.findMsgByCode(errcode);
            throw new IllegalArgumentException(msgByCode);
        }
    }
}

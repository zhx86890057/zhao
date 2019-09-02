package com.zhao.upms.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhao.upms.web.constant.WxCpApiPathConsts;
import com.zhao.upms.web.constant.WxCpErrorMsgEnum;
import com.zhao.upms.web.wxBean.WxCpDepart;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WxDepartServiceImpl {
    @Autowired
    private RestTemplate restTemplate;

    public Long create(WxCpDepart wxCpDepart, String accessToken){
        String url = WxCpApiPathConsts.Department.DEPARTMENT_CREATE + accessToken;
        JSONObject jsonObject = restTemplate.postForObject(url, JSON.toJSONString(wxCpDepart), JSONObject.class);
        checkErrCode(jsonObject);
        return jsonObject.getLong("id");
    }

    public void update(WxCpDepart wxCpDepart,String accessToken){
        String url = WxCpApiPathConsts.Department.DEPARTMENT_UPDATE + accessToken;
        JSONObject jsonObject = restTemplate.postForObject(WxCpApiPathConsts.getURL(url), JSONObject.toJSONString(wxCpDepart),
                JSONObject.class);
        checkErrCode(jsonObject);
    }

    public void delete(String departId, String accessToken){
        String url = String.format(WxCpApiPathConsts.Department.DEPARTMENT_DELETE, accessToken, departId);
        JSONObject jsonObject = restTemplate.getForObject(WxCpApiPathConsts.getURL(url), JSONObject.class);
        checkErrCode(jsonObject);
    }

    public List<WxCpDepart> list(String departId, String accessToken){
        Map map = new HashMap();
        map.put("access_token", accessToken);
        if(StringUtils.isNotBlank(departId)) map.put("id", departId);
        JSONObject jsonObject =
                restTemplate.getForObject(WxCpApiPathConsts.getURL(WxCpApiPathConsts.Department.DEPARTMENT_LIST), JSONObject.class, map);
        checkErrCode(jsonObject);
        List<WxCpDepart> list = jsonObject.getJSONArray("department").toJavaList(WxCpDepart.class);
        return list;
    }

    public void checkErrCode(JSONObject jsonObject){
        int errcode = jsonObject.getIntValue("errcode");
        if(errcode != WxCpErrorMsgEnum.CODE_0.getCode()){
            String msgByCode = WxCpErrorMsgEnum.findMsgByCode(errcode);
            throw new IllegalArgumentException(msgByCode);
        }
    }
}

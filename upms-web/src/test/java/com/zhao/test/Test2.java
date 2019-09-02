package com.zhao.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhao.dao.domain.WxCorp;
import com.zhao.upms.web.UpmsApplication;
import com.zhao.upms.web.service.impl.WxAPI;
import com.zhao.upms.web.wxBean.WxAccessToken;
import com.zhao.upms.web.wxBean.WxAuthCorpInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UpmsApplication.class)
@Transactional //支持事物，@SpringBootTest 事物默认自动回滚
@Rollback(false) // 事务自动回滚，不自动回滚@Rollback(false)
public class Test2 {

    @Autowired
    WebApplicationContext context;
    @Autowired
    private RestTemplate restTemplate;

    private MockMvc mvc;

    @Before
    public void initTests() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void test(){
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token";
        Map<String, String> params= new HashMap<>();
        params.put("corpid", "wwb74a233bc7df7597");
        params.put("provider_secret", "t9I66qyf90SXbaUtZ_v9loNvj2-hvMZQm6XDI7t_bCqmOT8UZcI4HiN3scZt70OV");
        JSONObject jsonObject = restTemplate.postForObject(url, JSON.toJSONString(params), JSONObject.class);
        System.out.println(jsonObject.get("provider_access_token"));
    }

    @Autowired
    private WxAPI wxAPI;

    @Test
    public void test2(){
        String permanentCode = "sf66hs80-LElRHGyavvkmGj24RgFPLhhVW7KV-7denE";
        WxCorp wxAPIAuthInfo = wxAPI.getAuthInfo("8qibKlIP0fBCR1FvfoxLTrQuzGRXmzYQAyjdINfo1GR9kfIFmI16aUMqMEGS29" +
                "-duhNhbDY7G7_4iuBz5BEmVuzYxOJ28QsNsdrMNNQOm2H5pTZnYJVvo1VzXi77K2io","ww70559ce8c6d3a12d", permanentCode);
        WxAccessToken wxAccessToken = wxAPI.getCorpToken("8qibKlIP0fBCR1FvfoxLTrQuzGRXmzYQAyjdINfo1GR9kfIFmI16aUMqMEGS29-duhNhbDY7G7_4iuBz5BEmVuzYxOJ28QsNsdrMNNQOm2H5pTZnYJVvo1VzXi77K2io","ww70559ce8c6d3a12d", permanentCode);
        System.out.println(wxAPIAuthInfo);
        System.out.println(wxAccessToken);
    }

}

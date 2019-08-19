package com.zhao.test;

import com.zhao.upms.web.UpmsApplication;
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
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("corpid", "wwb74a233bc7df7597");
        params.add("provider_secret", "t9I66qyf90SXbaUtZ_v9loNvj2-hvMZQm6XDI7t_bCqmOT8UZcI4HiN3scZt70OV");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(params, new HttpHeaders());
        String s = restTemplate.postForObject(url, requestEntity, String.class);
        System.out.println(s);
    }

}

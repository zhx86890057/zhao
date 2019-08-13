package com.zhao.test;

import com.alibaba.fastjson.JSON;
import com.zhao.dao.domain.SysUser;
import com.zhao.upms.common.api.CommonResult;
import com.zhao.upms.web.UpmsApplication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UpmsApplication.class)
@Transactional //支持事物，@SpringBootTest 事物默认自动回滚
@Rollback(false) // 事务自动回滚，不自动回滚@Rollback(false)
public class TestExample {

    private Log logger= LogFactory.getLog(TestExample.class);

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void initTests() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void shouldHaveEmptyDB() throws Exception {
        String responseString = mvc.perform(
                get("/test")    //请求的url,请求的方法是get
                        .contentType(MediaType.APPLICATION_JSON)  //数据的格式
                        .param("pcode","root")         //添加参数
                ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        logger.info("--------返回的json = " + responseString);
    }

    @Test
    public void register() throws Exception {
        SysUser sysUser = SysUser.builder().id(1L).username("zhao").password("123456").build();
        String responseString = mvc.perform(
                post("/admin/register")    //请求的url,请求的方法是get
                        .contentType(MediaType.APPLICATION_JSON)  //数据的格式
                        .content(JSON.toJSONBytes(sysUser))//添加参数
        ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        logger.info("--------返回的json = " + responseString);
    }

    @Test
    public void login() throws Exception {
        SysUser sysUser = SysUser.builder().username("zhao").password("123456").build();
        String responseString = mvc.perform(
                post("/admin/login")    //请求的url,请求的方法是get
                        .contentType(MediaType.APPLICATION_JSON)  //数据的格式
                        .content(JSON.toJSONBytes(sysUser))//添加参数
        ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        CommonResult commonResult = JSON.parseObject(responseString, CommonResult.class);

        String responseString2 = mvc.perform(
                get("/admin/info")    //请求的url,请求的方法是get
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)  //数据的格式
                        .param("access_token", (String) commonResult.getData())//添加参数
        ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        logger.info("--------返回的json = " + responseString2);
    }

}

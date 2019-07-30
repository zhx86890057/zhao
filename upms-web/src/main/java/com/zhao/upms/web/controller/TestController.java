package com.zhao.upms.web.controller;

import com.zhao.dao.domain.Test;
import com.zhao.dao.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestController {

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/test")
    public Test test(){
        Test test = new Test();
        test.setModifyTime(new Date());
        testMapper.insert(test);
        return testMapper.selectByPrimaryKey(2);
    }

}
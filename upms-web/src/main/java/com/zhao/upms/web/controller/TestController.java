package com.zhao.upms.web.controller;

import com.zhao.dao.domain.Test;
import com.zhao.dao.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;

@RestController
public class TestController {

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/test")
//    @PreAuthorize("hasRole('admin')")
    public int test(Principal user){
        Test test = new Test();
        test.setModifyTime(new Date());
        testMapper.insert(test);
        test.setId(1);
        test.setCreatTime(new Date());
        return testMapper.updateByPrimaryKey(test);
    }

}

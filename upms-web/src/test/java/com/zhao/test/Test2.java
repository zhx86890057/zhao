package com.zhao.test;

import com.zhao.dao.domain.School;
import com.zhao.upms.web.UpmsApplication;
import com.zhao.upms.web.service.impl.SchoolService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by hasee on 2018/8/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UpmsApplication.class)
public class Test2 {

    @Autowired
    private SchoolService schoolService;

    @Test
    public void test(){
        schoolService.insertSchoolBatch();
    }
}

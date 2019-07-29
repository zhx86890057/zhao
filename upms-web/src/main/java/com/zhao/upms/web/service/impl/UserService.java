package com.zhao.upms.web.service.impl;

import com.zhao.dao.domain.User;
import com.zhao.dao.mapper.UserMapper;
import com.zhao.dao.vo.UserListResultVO;
import com.zhao.upms.web.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public UserListResultVO getUserList(String schoolid){
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("objectid", schoolid);
        map.put("objType", "2");
        map.put("page", 1);
        map.put("size", "9999");

        String sign = SignUtil.getSign(map);
        map.put("sign", sign);

        String url = "https://open.campus.qq.com/api/open/buGetUserList?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&objectid={objectid}&objType={objType}&officeid={officeid}&page={page}&size={size}";
        UserListResultVO res = restTemplate.getForObject(url, UserListResultVO.class, map);
        return res;
    }

    public Integer insertUser(String schoolid){
        UserListResultVO userListResultVO = this.getUserList(schoolid);
        if(userListResultVO.getCode() == 0){
            List<User> dataList = userListResultVO.getData().getDataList();
            return userMapper.insertBatch(dataList);
        }else {
            log.info("获取家长列表：{}", userListResultVO.getMsg());
        }
        return 0;
    }

}

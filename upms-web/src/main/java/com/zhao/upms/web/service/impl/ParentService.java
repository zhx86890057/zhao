package com.zhao.upms.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhao.dao.domain.Parent;
import com.zhao.dao.mapper.ParentMapper;
import com.zhao.dao.vo.ParentListResultVO;
import com.zhao.upms.web.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j
public class ParentService {
    @Autowired
    private ParentMapper parentMapper;

    public ParentListResultVO getParentList(String schoolid){
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

        String url = "https://open.campus.qq.com/api/open/buGetParentList?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&objectid={objectid}&objType={objType}&officeid={officeid}&page={page}&size={size}";
        ParentListResultVO res = restTemplate.getForObject(url, ParentListResultVO.class, map);
        return res;
    }

    public Integer insertParent(String schoolid){
        ParentListResultVO parentList = this.getParentList(schoolid);
        if(parentList.getCode() == 0){
            List<Parent> dataList = parentList.getData().getDataList();
            if(!CollectionUtils.isEmpty(dataList)){
                return parentMapper.insertBatch(dataList);
            }
        }else {
            log.info("获取家长列表：{}", parentList.getMsg());
        }
        return 0;
    }
}

package com.zhao.upms.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.zhao.dao.domain.Depart;
import com.zhao.dao.domain.Parent;
import com.zhao.dao.mapper.DepartMapper;
import com.zhao.dao.vo.DepartListResultVO;
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
public class DepartService {
    @Autowired
    private DepartMapper departMapper;

    public DepartListResultVO getDepartList(String schoolid, int type){
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("objectid", schoolid);
        map.put("objType", "2");
        map.put("type", type);
        map.put("page", 1);
        map.put("size", "9999");
        String sign = SignUtil.getSign(map);
        map.put("sign", sign);
        String url = "https://open.campus.qq.com/api/open/buGetDepartList?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&objectid={objectid}&objType={objType}&officeid={officeid}&type={type}&page={page}&size={size}";
        DepartListResultVO res = restTemplate.getForObject(url, DepartListResultVO.class, map);
        return res;
    }

    public Integer insertDepart(String schoolid){
        for(int i=1; i<=5; i++){
            DepartListResultVO departListResultVO = this.getDepartList(schoolid, i);
            if(departListResultVO.getCode() == 0){
                List<Depart> dataList = departListResultVO.getData().getDataList();
                if(!CollectionUtils.isEmpty(dataList)){
                    departMapper.insertBatch(dataList);
                }
            }else {
                log.info("获取部门列表：{}",departListResultVO.getMsg());
            }
        }
        return 1;
    }
}

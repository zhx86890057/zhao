package com.zhao.upms.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhao.dao.domain.School;
import com.zhao.dao.mapper.SchoolMapper;
import com.zhao.dao.vo.SchoolInfoResultVO;
import com.zhao.dao.vo.SchoolListResultVO;
import com.zhao.dao.vo.SchoolListVO;
import com.zhao.upms.web.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j
public class SchoolService {

    @Autowired
    private SchoolMapper schoolMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ParentService parentService;
    @Autowired
    private DepartService departService;

    public Integer insertSchoolBatch(){
        SchoolListResultVO resultVO = this.getSchoolList(1);
        if(resultVO.getCode() == 0){
            List<SchoolListVO.DataList> dataList = resultVO.getData().dataList;
            for(SchoolListVO.DataList data: dataList){
                String objectid = data.getObjectid();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        departService.insertDepart(objectid);
                        userService.insertUser(objectid);
                        parentService.insertParent(objectid);
                        SchoolInfoResultVO schoolInfo = getSchoolInfo(objectid);
                        if (schoolInfo.getCode() == 0) {
                            School school = schoolInfo.getData();
                            school.setObjectid(objectid);
                            insertSchool(school);
                        } else {
                            log.info("获取学校列表：{}", schoolInfo.getMsg());
                        }
                    }
                }).start();
            }
        }
        return 1;
    }

    public SchoolListResultVO getSchoolList(int page) {
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("page", page);
        map.put("size", "9999");
        String sign = SignUtil.getSign(map);
        map.put("sign", sign);

        String url = "https://open.campus.qq.com/api/open/buGetschoolList?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&officeid={officeid}&page={page}&size={size}";
        SchoolListResultVO res = restTemplate.getForObject(url, SchoolListResultVO.class, map);
        return res;
    }

    public SchoolInfoResultVO getSchoolInfo(String schoolid) {
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("objectid", schoolid);
        map.put("objType", "2");

        String sign = SignUtil.getSign(map);
        map.put("sign", sign);

        String url = "https://open.campus.qq.com/api/open/buGetschoolInfo?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&objectid={objectid}&objType={objType}&officeid={officeid}";
        JSONObject res = restTemplate.getForObject(url, JSONObject.class, map);
        SchoolInfoResultVO school = res.toJavaObject(SchoolInfoResultVO.class);
        return school;
    }

    public Integer insertSchool(School school){
        int line = schoolMapper.insert(school);
        return line;
    }
}

package com.zhao.test;

import com.alibaba.fastjson.JSONObject;
import com.zhao.dao.vo.SchoolInfoResultVO;
import com.zhao.dao.vo.SchoolListResultVO;
import com.zhao.dao.vo.SchoolListVO;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.util.*;

public class Test1 {
    public static void main(String[] args) {

//        getUserList("sHjunemvTtOsFEdone");
//        getParentList("sHjunemvTtOsFEdone");
//        getDepartList("sHjunemvTtOsFEdone", 2);

    }

    public static void getDepartList(String schoolid, int type){
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("objectid", schoolid);
        map.put("objType", "2");
        map.put("type", type);

        String sign = getSign(map);
        map.put("sign", sign);

        String url = "https://open.campus.qq.com/api/open/buGetDepartList?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&objectid={objectid}&objType={objType}&officeid={officeid}&type={type}";
        JSONObject res = restTemplate.getForObject(url, JSONObject.class, map);
        System.out.println(res);
    }

    public static void getParentList(String schoolid){
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("objectid", schoolid);
        map.put("objType", "2");

        String sign = getSign(map);
        map.put("sign", sign);

        String url = "https://open.campus.qq.com/api/open/buGetParentList?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&objectid={objectid}&objType={objType}&officeid={officeid}";
        JSONObject res = restTemplate.getForObject(url, JSONObject.class, map);
        System.out.println(res);
    }

    public static void getUserList(String schoolid){
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("objectid", schoolid);
        map.put("objType", "2");
        map.put("page",1);
        map.put("size",1000);
        String sign = getSign(map);
        map.put("sign", sign);

        String url = "https://open.campus.qq.com/api/open/buGetUserList?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&objectid={objectid}&objType={objType}&officeid={officeid}&page={page}&size={size}";
        JSONObject res = restTemplate.getForObject(url, JSONObject.class, map);
        System.out.println(res);
    }

    public static void getSchoolInfo(String schoolid) {
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("objectid", schoolid);
        map.put("objType", "2");

        String sign = getSign(map);
        map.put("sign", sign);

        String url = "https://open.campus.qq.com/api/open/buGetschoolInfo?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&objectid={objectid}&objType={objType}&officeid={officeid}";
        JSONObject res = restTemplate.getForObject(url, JSONObject.class, map);
        System.out.println(res);
    }

    public static void getSchoolList() {
        RestTemplate restTemplate = new RestTemplate();
        TreeMap map = new TreeMap();
        map.put("timestamp", "" + System.currentTimeMillis());
        map.put("devCode", "3tYjQGzKXNXeNujF");
        map.put("devType", "1");
        map.put("officeid", "HVMLFRjunehWsMdone");
        map.put("page", 1);
        map.put("size", "100");
        String sign = getSign(map);
        map.put("sign", sign);

        String url = "https://open.campus.qq.com/api/open/buGetschoolList?timestamp={timestamp}&sign={sign}&devCode={devCode}&devType={devType}" +
                "&officeid={officeid}&page={page}&size={size}";
        SchoolListResultVO res = restTemplate.getForObject(url, SchoolListResultVO.class, map);

        if(res.getCode() == 0){
            List<SchoolListVO.DataList> dataList = res.getData().dataList;
            System.out.println(dataList);
        }
    }


    public static String getSign(TreeMap map) {
        Set<Map.Entry> set = map.entrySet();
        StringBuffer sb = new StringBuffer();
        //取出排序后的参数，逐一连接起来
        for (Iterator<Map.Entry> it = set.iterator(); it.hasNext(); ) {
            Map.Entry me = it.next();
            sb.append(me.getKey() + "=" + me.getValue() + "&");
        }
        String secret = "377729078cacd0711f00d4df8df260a3";
        sb.append("key=" + secret);
        String sign = DigestUtils.md5DigestAsHex(sb.toString().getBytes()).toUpperCase();
        return sign;
    }
}

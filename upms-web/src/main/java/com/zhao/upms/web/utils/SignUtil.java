package com.zhao.upms.web.utils;

import org.springframework.util.DigestUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SignUtil {
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

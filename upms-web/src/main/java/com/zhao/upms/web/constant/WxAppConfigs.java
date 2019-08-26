package com.zhao.upms.web.constant;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Map;

public class WxAppConfigs {
    public static final String corpId = "ww70559ce8c6d3a12d";

    public static final String suiteID = "wwc5d50093b12345b8";
    public static final String secret = "DV_LfaUwOlYP-BP_hw1-J0UPq4gF19U7a9LLt9cQSKs";
    public static final String token = "5FBgXQ2Y45f5PpgsqleZLnGaKAh2PL";
    public static final String encodingAESKey = "HJbK12JTl0pHzbwlgM5vskDUxzlmfwtN9BVaEpW2j7c";

    private static Map<String, AppConfig> configMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        configMap.put(suiteID, new AppConfig(suiteID, secret, token, encodingAESKey));
    }

    public static AppConfig getAppConfig(String suiteID){
        return configMap.get(suiteID);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class AppConfig {
        /**
         * 设置微信企业应用的suiteID
         */
        private String suiteID;

        /**
         * 设置微信企业应用的Secret
         */
        private String secret;

        /**
         * 设置微信企业号的token
         */
        private String token;

        /**
         * 设置微信企业号的EncodingAESKey
         */
        private String encodingAESKey;

    }
}

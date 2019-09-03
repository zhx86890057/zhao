package com.zhao.upms.web.constant;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class WxAppConfigs {
    public static final String corpId = "ww70559ce8c6d3a12d";

    public static final String suiteID = "wwc5d50093b12345b8";
    public static final String secret = "DV_LfaUwOlYP-BP_hw1-J0UPq4gF19U7a9LLt9cQSKs";
    public static final String token = "5FBgXQ2Y45f5PpgsqleZLnGaKAh2PL";
    public static final String encodingAESKey = "HJbK12JTl0pHzbwlgM5vskDUxzlmfwtN9BVaEpW2j7c";


    public static final String suiteID2 = "ww3d23b50f43283627";
    public static final String secret2 = "rm16HcIAmBpJlozy1nNx9LV-WduFiYT6hclnkMhpuJA";
    public static final String token2 = "fiQqzaBgawMgn";
    public static final String encodingAESKey2 = "p54bgbSHV7XmTK5WrjsCdgndEDeaAFvRYtHG3ZDoto0";
    private static Map<String, AppConfig> configMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        configMap.put(suiteID,
                AppConfig.builder().suiteID(suiteID).secret(secret).token(token).encodingAESKey(encodingAESKey).build());
        configMap.put(suiteID2,
                AppConfig.builder().suiteID(suiteID2).secret(secret2).token(token2).encodingAESKey(encodingAESKey2).build());
    }

    public static AppConfig getAppConfig(String suiteID){
        return configMap.get(suiteID);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
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

        //安全凭证
        private String suiteTicket;
        //第三方应用凭证
        private volatile String suiteAccessToken;
        //第三方应用凭证过期时间
        private volatile long expiresTime;

        public void updateSuiteAccessToken(String suiteAccessToken, int expiresInSeconds) {
            this.suiteAccessToken = suiteAccessToken;
            this.expiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000L;
        }
    }
}

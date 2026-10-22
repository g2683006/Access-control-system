package com.rookie.rookiemeeting.config;

import com.baidu.aip.face.AipFace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaiduConfig {

    //没配置百度人脸识别时为空，PhotoController 会走演示模式，不会真的调用百度接口
    @Value("${baidu.appId:}")
    private String appId;

    @Value("${baidu.key:}")
    private String key;

    @Value("${baidu.secret:}")
    private String secret;

    @Bean
    public AipFace aipFace() {
        return new AipFace(appId, key, secret);
    }
}

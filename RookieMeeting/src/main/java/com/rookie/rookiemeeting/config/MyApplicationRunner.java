package com.rookie.rookiemeeting.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

/**
 * 启动清理缓存
 */
@Configuration
@Slf4j
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //只删本系统的键（MyBatis 二级缓存和登录 token），不要把同一个 Redis 库里别的程序的数据也清掉
        Set keys = redisTemplate.keys("*com.rookie.rookiemeeting.mapper*");
        keys.addAll(redisTemplate.keys("JWT_*"));
        redisTemplate.delete(keys);
        log.info("清理redis缓存 {} 个键", keys.size());
    }
}

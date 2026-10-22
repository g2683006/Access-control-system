package com.rookie.rookiemeeting.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rookie.rookiemeeting.entity.Employee;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * 接口返回的 JSON 的统一设置（只影响接口，不影响 Redis 里的缓存）
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder
                //时间统一用 "yyyy-MM-dd HH:mm:ss"，按本机时区解析和输出。Jackson 默认按 UTC 输出，页面上的时间会和数据库差几个小时
                .simpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .timeZone(TimeZone.getDefault())
                //员工的密码只接收不返回，否则员工列表接口会把所有人的密码哈希发给前端
                .mixIn(Employee.class, EmployeeJsonMixin.class);
    }

    abstract static class EmployeeJsonMixin {
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String password;
    }
}

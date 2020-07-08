package com.j.openproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.j.openproject.injector.IncSqlInjector;

/**
 * @author Joyuce
 * @Type MyBatisPlusConfig
 * @Desc 配置类
 * @date 2020年02月20日
 * @Version V1.0
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public ISqlInjector getSqlInjector(){
        return new IncSqlInjector();
    }
}

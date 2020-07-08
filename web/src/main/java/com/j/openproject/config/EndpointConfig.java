package com.j.openproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author joyuce
 * @Type EndpointConfig
 * @Desc
 * @date 2019年06月10日
 * @Version V1.0
 */
@Configuration
public class EndpointConfig {
    @Bean
    public EndpointConfigure newConfigure()
    {
        return new EndpointConfigure();
    }
}

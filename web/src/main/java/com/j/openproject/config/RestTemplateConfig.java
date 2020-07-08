package com.j.openproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Joyuce
 * @Type RestTemplateConfig
 * @Desc RestTemplate配置
 * @date 2019年11月22日
 * @Version V1.0
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 链接超时 ms
     */
    private int connectTimeout = 100 * 1000;
    /**
     * 读取超时 ms
     */
    private int readTimeout = 200 * 1000;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(readTimeout);
        factory.setConnectTimeout(connectTimeout);
        return factory;
    }

}

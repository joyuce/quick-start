package com.j.openproject.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${app.version:0.0.1}")
    private String version;

    @Value("${swagger.show:false}")
    private String openSwagger;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决 doc.html 404 报错
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");

    }

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).enable(isDev()).select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("接口 API")
                //版本号
                .version(version)
                //描述
                .description("后端接口文档").build();
    }

    private boolean isDev() {
        if (StringUtils.isNotBlank(openSwagger) && "true".equalsIgnoreCase(openSwagger)) {
            return true;
        }
        return false;
    }

}

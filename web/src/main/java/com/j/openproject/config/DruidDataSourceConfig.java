package com.j.openproject.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.WebStatFilter;
import com.j.openproject.servlet.DruidStatViewServlet;

/**
 * @author Joyuce
 * @Type DruidDataSourceConfig
 * @Desc
 * @date 2019年06月13日
 * @Version V1.0
 */
@Configuration
public class DruidDataSourceConfig {

    //@Value("${druid.ip.allow}")
    private String allowIP;

    //@Value("${druid.ip.deny}")
    private String denyIP;

    @Value("${druid.login.user.name}")
    private String loginUsername;

    @Value("${druid.login.user.password}")
    private String loginPassword;

    /**
     * 配置监控服务器
     *
     * @return 返回监控注册的servlet对象
     * @author SimpleWu
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new DruidStatViewServlet(),"/druid/*");
        // 添加IP白名单 /** 白名单，如果不配置或value为空，则允许所有 */
        if (StringUtils.isNotBlank(allowIP)){
            servletRegistrationBean.addInitParameter("allow", allowIP);
        }
        // 添加IP黑名单，当白名单和黑名单重复时，黑名单优先级更高
        if (StringUtils.isNotBlank(denyIP)){
            servletRegistrationBean.addInitParameter("deny", denyIP);
        }
        // 添加控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername", loginUsername);
        servletRegistrationBean.addInitParameter("loginPassword", loginPassword);
        // 是否能够重置数据
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    /**
     * 配置服务过滤器
     *
     * @return 返回过滤器配置对象
     */
    @Bean
    public FilterRegistrationBean statFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        // 添加过滤规则
        filterRegistrationBean.addUrlPatterns("/*");
        // 忽略过滤格式
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,");
        return filterRegistrationBean;
    }
}

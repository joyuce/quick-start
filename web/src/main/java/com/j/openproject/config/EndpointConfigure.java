package com.j.openproject.config;

import javax.websocket.server.ServerEndpointConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author joyuce
 * @Type EndpointConfigure
 * @Desc 支持注入
 * @date 2019年06月10日
 * @Version V1.0
 */
public class EndpointConfigure extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

    private static BeanFactory context;

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return context.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EndpointConfigure.context = applicationContext;
    }
}
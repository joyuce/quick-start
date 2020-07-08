package com.j.openproject.controller;

import java.util.concurrent.ExecutorService;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.j.openproject.thread.PoolService;
import com.j.openproject.utils.HttpRequestUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type BaseController
 * @Desc 基础控制器
 * @date 2019年11月28日
 * @Version V1.0
 */
@Slf4j
@Component
public abstract class BaseController {

    @Autowired
    private PoolService poolService;

    protected ExecutorService taskPool(){
        return poolService.getPool();
    }

    private final String UNKNOWN = "unknown";

    private final String INDEX = ",";

    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public String getIpAddress() {
        return getIpAddr(getRequest());
    }

    public Integer getUserId() {
        return (Integer) getRequest().getAttribute("userId");
    }

    /**
     * 获取当前请求的用户的IP
     *
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        return HttpRequestUtil.getIpAddr(request);
    }

}

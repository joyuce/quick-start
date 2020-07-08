package com.j.openproject.servlet;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.druid.support.http.StatViewServlet;
import com.j.openproject.utils.HttpRequestUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author shenxiaodong
 * @Type DruidStatViewServlet
 * @Desc druid重写Servlet 打印登录ip
 * @date 2019年06月17日
 * @Version V1.0
 */
@Slf4j
public class DruidStatViewServlet extends StatViewServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isPermittedRequest(HttpServletRequest request) {
        String remoteAddress = getRemoteIpAddr(request);
        return isPermittedRequest(remoteAddress);
    }

    private String getRemoteIpAddr(HttpServletRequest request) {
        String ip = HttpRequestUtil.getIpAddr(request);
        log.debug("尝试或已经登录druid的ip：" + ip);
        return ip;
    }
}

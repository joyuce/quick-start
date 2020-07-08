package com.j.openproject.auth.data;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joyuce
 * @Type UserAuthGet
 * @Desc 用户权限获取
 * @date 2019年11月20日
 * @Version V1.0
 */
public interface UserAuthGet {

    /**
     * 根据请求头获取用户的唯一标志（无法在异步中使用）
     *
     * @param request http请求
     * @return （约定的）用户唯一标志
     */
    public Object getUserCodeByRequest(HttpServletRequest request);

    /**
     * 获取方法上用户参数的名称
     *
     * @return （约定的）用户参数的名称
     */
    public String getUserMethodParamName();

    /**
     * 根据 方法上用户参数的值 获取用户的唯一标志（异步中使用）
     *
     * @param userMethodParamValue  方法上用户参数的值
     * @return （约定的）用户唯一标志
     */
    public Object getUserAuthByMethodParam(Object userMethodParamValue);

    /**
     * 获取用户权限
     *
     * @param userObject 约定的）用户唯一标志
     * @return 用户权限集合
     */
    public Set<String> getUserAuthByObject(Object userObject);

}

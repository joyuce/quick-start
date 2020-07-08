package com.j.openproject.auth;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.j.openproject.auth.data.UserAuthGet;

/**
 * @author Joyuce
 * @Type UserAuthGetImpl
 * @Desc
 * @date 2019年11月20日
 * @Version V1.0
 */
@Component
public class UserAuthGetImpl implements UserAuthGet {

    /**
     * 根据请求头获取用户的唯一标志（无法在异步中使用）
     *
     * @param request http请求
     * @return （约定的）用户唯一标志
     */
    @Override
    public Object getUserCodeByRequest(HttpServletRequest request) {
        return "sign";
    }

    /**
     * 获取方法上用户参数的名称
     *
     * @return （约定的）用户参数的名称
     */
    @Override
    public String getUserMethodParamName() {
        return "user";
    }

    /**
     * 根据 方法上用户参数的值 获取用户的唯一标志（异步中使用）
     *
     * @param userMethodParamValue 方法上用户参数的值
     * @return （约定的）用户唯一标志
     */
    @Override
    public Object getUserAuthByMethodParam(Object userMethodParamValue) {
        return "sign";
    }

    /**
     * 获取用户权限
     *
     * @param userObject 约定的）用户唯一标志
     * @return 用户权限集合
     */
    @Override
    public Set<String> getUserAuthByObject(Object userObject) {
        if ("sign".equals(userObject)) {
            Set<String> set = new HashSet<>();
            set.add("4");
            return set;
        }
        return null;
    }
}

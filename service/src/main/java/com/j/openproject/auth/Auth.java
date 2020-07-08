package com.j.openproject.auth;

import org.springframework.context.annotation.Configuration;

import com.j.openproject.auth.code.AuthCode;

/**
 * @author Joyuce
 * @Type Auth
 * @Desc 权限
 * @date 2019年11月25日
 * @Version V1.0
 */
@Configuration
public class Auth {

    /**
     * 定义的权限代码常量
     */
    public static final String ONE = "1";

    /**
     * 定义的权限代码常量
     */
    public static final String TWO = "2";

    /**
     * 定义的权限代码常量
     */
    public static final String THREE = "3";

    /**
     * 保存代码，关联的父级权限存在的情况，在子级无权限时，校验父级权限；父子关系，父包含子。
     */
    static {
        AuthCode.saveCode(ONE, "111", "0");
        AuthCode.saveCode(TWO, "222", "1");
        AuthCode.saveCode(THREE, "333", "2");
    }
}

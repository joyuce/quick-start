package com.j.openproject.annotation;

import java.lang.annotation.*;

/**
 * @author Joyuce
 * @Type NoLogin
 * @Desc 登录不校验注解
 * @date 2020年03月09日
 * @Version V1.0
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLogin {
}

package com.j.openproject.auth.annotation;

import java.lang.annotation.*;

/**
 * @author Joyuce
 * @Type AuthCheck
 * @Desc 权限校验注解
 * @date 2019年11月14日
 * @Version V1.0
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthCheck {

    /**
     * 需要校验的权限代码
     *
     */
    String[] codes();

    /**
     * 多个权限校验时，校验结果是否为： true 与关系 (一个校验不通过即不通过)  false 或关系 (一个校验通过即通过)
     *
     */
    boolean isAnd() default true;

}

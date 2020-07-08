package com.j.openproject.utils;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * AopUtil
 *
 * @author Joyuce
 * @date 2020年03月31日
 */
public class AopUtil {

    /**
     * 获取注解对象
     *
     * @param joinPoint
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotationByPoint(JoinPoint joinPoint, Class<T> clazz) {
        //获取方法上的注解
        T annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(clazz);
        if (annotation == null) {
            //获取类上的注解
            annotation = joinPoint.getTarget().getClass().getAnnotation(clazz);
            if (annotation == null) {
                //获取接口上的注解
                for (Class<?> cls : joinPoint.getClass().getInterfaces()) {
                    annotation = cls.getAnnotation(clazz);
                    if (annotation != null) {
                        break;
                    }
                }
            }
        }
        return annotation;
    }

    private AopUtil() {
    }

    ;
}

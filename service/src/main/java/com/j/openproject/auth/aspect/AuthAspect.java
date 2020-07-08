package com.j.openproject.auth.aspect;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.j.openproject.auth.annotation.AuthCheck;
import com.j.openproject.auth.code.AuthCode;
import com.j.openproject.auth.data.UserAuthGet;
import com.j.openproject.auth.exception.AuthCheckException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type AuthAspect
 * @Desc 权限校验切面
 * @date 2019年11月14日
 * @Version V1.0
 */
@Aspect
@Component
@Slf4j
@Scope
public class AuthAspect {

    @Autowired
    private UserAuthGet userAuthGet;

    /**
     * 控制器切面
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.j.openproject.auth.annotation.AuthCheck)")
    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //获取用户权限
        Set<String> userAuthSet = null;
        Object userObject = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            String userMethodParamName = userAuthGet.getUserMethodParamName();
            if (userMethodParamName == null) {
                throw new AuthCheckException("获取不到（约定的）用户参数的名称");
            }
            Object userMethodParamValue = null;
            Object[] values = proceedingJoinPoint.getArgs();
            String[] names = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
            for (int i = 0; i < names.length; i++) {
                if (userMethodParamName.equals(names[i])) {
                    userMethodParamValue = values[i];
                }
            }
            if (userMethodParamValue == null) {
                throw new AuthCheckException("在方法上无法找到（约定的）用户参数的名称");
            }
            userObject = userAuthGet.getUserAuthByMethodParam(userMethodParamValue);
        } else {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            userObject = userAuthGet.getUserCodeByRequest(request);
        }
        if (userObject == null) {
            throw new AuthCheckException("获取不到（约定的）用户唯一标志");
        }
        userAuthSet = userAuthGet.getUserAuthByObject(userObject);
        if (userAuthSet == null) {
            throw new AuthCheckException("获取不到用户权限集合");
        }
        //校验权限
        AuthCheck authCheck = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod()
                .getAnnotation(AuthCheck.class);
        if (authCheck == null) {
            throw new AuthCheckException("获取注解实例异常");
        }
        boolean isAnd = authCheck.isAnd();
        String[] codes = authCheck.codes();
        if (isAnd) {
            for (String code : codes) {
                if (!userAuthSet.contains(code)) {
                    boolean checkFather = checkFatherCode(userAuthSet, code);
                    if (!checkFather) {
                        throw new AuthCheckException(
                                new StringBuilder().append("权限校验不通过，code=").append(code).append(",detail=")
                                        .append(AuthCode.getCodeDetail(code)).toString());
                    }
                }
            }
        } else {
            for (String code : codes) {
                if (!userAuthSet.contains(code)) {
                    boolean checkFather = checkFatherCode(userAuthSet, code);
                    if (checkFather) {
                        return proceedingJoinPoint.proceed();
                    }
                } else {
                    return proceedingJoinPoint.proceed();
                }
            }
            throw new AuthCheckException("权限校验不通过");
        }
        return proceedingJoinPoint.proceed();
    }

    /**
     * 校验父级权限
     *
     * @param userAuthSet
     * @param sonCode
     * @return
     */
    private boolean checkFatherCode(Set<String> userAuthSet, String sonCode) {
        for (; ; ) {
            String parentCode = AuthCode.getFatherCode(sonCode);
            if (parentCode == null) {
                break;
            }
            sonCode = parentCode;
            if (userAuthSet.contains(parentCode)) {
                return true;
            }
        }
        return false;
    }

}

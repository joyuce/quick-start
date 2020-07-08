package com.j.openproject.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.j.openproject.annotation.NoLogin;
import com.j.openproject.code.CommonRsCode;
import com.j.openproject.exception.AppException;
import com.j.openproject.utils.AopUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type LoginAop
 * @Desc 登录，接口耗时统计
 * @date 2020年03月04日
 * @Version V1.0
 */
@Aspect
@Component
@Slf4j
@Scope
public class LoginAop {

    @Around("execution(public * com.j.openproject.controller.*.*(..))")
    public Object doAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        String rqUrl = "接口：";
        long start = System.currentTimeMillis();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            rqUrl = rqUrl + request.getRequestURI();
        } else {
            rqUrl = rqUrl + "非http";
        }
        try {
            //校验是否登录
            NoLogin noLogin = AopUtil.getAnnotationByPoint(joinPoint, NoLogin.class);
            if (noLogin == null) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                String token = request.getHeader("token");
                if (!checkToken(token)) {
                    throw new AppException(CommonRsCode.LOGIN_ERROR);
                }
            }
            return joinPoint.proceed();
        } finally {
            long time = System.currentTimeMillis() - start;
            String str = rqUrl + " 耗时：" + time + " 毫秒";
            log.info(str);
        }
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     */
    private boolean checkToken(String token) {
        return false;
    }

}

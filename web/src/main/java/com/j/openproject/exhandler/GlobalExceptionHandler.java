package com.j.openproject.exhandler;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.j.openproject.auth.exception.AuthCheckException;
import com.j.openproject.base.CommonRs;
import com.j.openproject.code.CommonRsCode;
import com.j.openproject.exception.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type ExceptionHandler
 * @Desc 控制层异常捕获处理器
 * @date 2019年11月21日
 * @Version V1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 缺少参数异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({ MissingServletRequestParameterException.class })
    @ResponseBody
    public CommonRs requestMissingServletRequest(MissingServletRequestParameterException ex) {
        CommonRs rs = CommonRs.createWithCode(CommonRsCode.VALID_ERROR);
        rs.setData("缺少必要参数,参数名称为" + ex.getParameterName());
        return rs;
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonRs notValidExceptionHandler(MethodArgumentNotValidException e) {
        CommonRs rs = CommonRs.createWithCode(CommonRsCode.VALID_ERROR);
        rs.setData(getErrorMsg(e.getBindingResult().getAllErrors()));
        return rs;
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public CommonRs exceptionHandler(Exception e) {
        log.error("捕获到未知Exception异常", e);
        return CommonRs.createWithCode(CommonRsCode.INT_ERROR);
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @ExceptionHandler(value = AuthCheckException.class)
    public CommonRs authCheckExceptionHandler(AuthCheckException e) {
        CommonRs rs = CommonRs.createWithCode(CommonRsCode.AUTH_CHECK_ERROR);
        rs.setData(e.getMessage());
        return rs;
    }

    @ResponseBody
    @ExceptionHandler(value = AppException.class)
    public CommonRs appExceptionHandler(AppException e) {
        log.info(new StringBuilder().append("业务异常 code:").append(e.getResultCode().getCode()).append(" msg:")
                                    .append(e.getResultCode().getCnMsg()).toString());
        return CommonRs.createWithCode(e.getResultCode());
    }

    @ResponseBody
    @ExceptionHandler(value = NullPointerException.class)
    public CommonRs nullExceptionHandler(NullPointerException e) {
        log.error("捕获到空指针异常", e);
        return CommonRs.createWithCode(CommonRsCode.INT_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public CommonRs methodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.info("前端请求方式错误 msg:" + e.getLocalizedMessage());
        return CommonRs.createWithCode(CommonRsCode.REQUEST_ERROR);
    }

    private String getErrorMsg(List<ObjectError> allErrors) {
        StringBuilder message = new StringBuilder();
        for (ObjectError error : allErrors) {
            message.append(error.getDefaultMessage()).append(" & ");
        }
        return message.substring(0, message.length() - 3);
    }

    /**
     * 参数校验异常
     *
     * @param e
     * @return
     */
    @SuppressWarnings("unchecked")
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public CommonRs handleConstraintViolationException(ConstraintViolationException e) {
        CommonRs rs = CommonRs.createWithCode(CommonRsCode.VALID_ERROR);
        rs.setData(getErrorMsgByEx(e));
        return rs;
    }

    /**
     * 参数校验错误信息处理
     *
     * @param exception
     * @return
     */
    private String getErrorMsgByEx(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        StringBuilder builder = new StringBuilder();
        for (ConstraintViolation violation : violations) {
            builder.append(violation.getMessage());
        }
        return builder.toString();
    }

}

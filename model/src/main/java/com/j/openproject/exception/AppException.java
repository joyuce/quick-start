package com.j.openproject.exception;

import com.j.openproject.code.ResultCode;

/**
 * @author Joyuce
 * @Type AppException
 * @Desc 后台运行异常
 * @date 2019年11月21日
 * @Version V1.0
 */
public class AppException extends RuntimeException {

    /**
     * 结果码对象
     */
    private ResultCode resultCode;

    public AppException(ResultCode resultCode) {
        super(resultCode.getCnMsg());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}

package com.zhao.upms.common.api;

public class CommonException extends Exception{

    private static final long serialVersionUID = -8198281171334131008L;

    private int errCode;

    public CommonException() {}

    public CommonException(int errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return this.errCode;
    }
}

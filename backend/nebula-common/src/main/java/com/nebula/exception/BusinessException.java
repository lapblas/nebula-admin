package com.nebula.exception;

/**
 * 业务异常类，用于处理所有业务相关的异常
 */
public class BusinessException extends RuntimeException {
    /**
     * 异常码
     */
    private int code;

    /**
     * 无参构造方法
     */
    public BusinessException() {
        super();
    }

    /**
     * 构造方法
     * @param message 异常信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500; // 默认异常码
    }

    /**
     * 构造方法
     * @param code 异常码
     * @param message 异常信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造方法
     * @param message 异常信息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500; // 默认异常码
    }

    /**
     * 构造方法
     * @param code 异常码
     * @param message 异常信息
     * @param cause 异常原因
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 获取异常码
     * @return 异常码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置异常码
     * @param code 异常码
     */
    public void setCode(int code) {
        this.code = code;
    }
}

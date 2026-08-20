package com.nebula.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用响应类，使用泛型来支持不同类型的数据
 * @param <T> 响应数据的类型
 */
@Setter
@Getter
public class Response<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 无参构造器
     */
    public Response() {
    }

    /**
     * 全参构造器
     * @param code 响应码
     * @param message 响应消息
     * @param data 响应数据
     */
    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应
     * @param <T> 响应数据的类型
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "操作成功", data);
    }

    /**
     * 创建成功响应（无数据）
     * @param <T> 响应数据的类型
     * @return 成功响应
     */
    public static <T> Response<T> success() {
        return new Response<>(200, "操作成功", null);
    }

    /**
     * 创建成功响应（自定义消息）
     * @param <T> 响应数据的类型
     * @param message 响应消息
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }

    /**
     * 创建成功响应（自定义响应码和响应数据）
     * @param <T> 响应数据的类型
     * @param code 响应码
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> Response<T> success(int code, T data) {
        return new Response<>(code, "操作成功", data);
    }

    /**
     * 创建成功响应（自定义响应码和消息）
     * @param <T> 响应数据的类型
     * @param code 响应码
     * @param message 响应消息
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> Response<T> success(int code, String message, T data) {
        return new Response<>(code, message, data);
    }

    /**
     * 创建失败响应
     * @param <T> 响应数据的类型
     * @param code 响应码
     * @param message 响应消息
     * @return 失败响应
     */
    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    /**
     * 创建失败响应（默认响应码）
     * @param <T> 响应数据的类型
     * @param message 响应消息
     * @return 失败响应
     */
    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

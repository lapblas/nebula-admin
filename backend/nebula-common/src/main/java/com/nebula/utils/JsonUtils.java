package com.nebula.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JSON工具类，提供对象与JSON之间的转换功能
 */
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 注册Java 8时间模块
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 配置时间格式为字符串而不是时间戳
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略未知属性（反序列化时）
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonUtils() {
    }

    /**
     * 对象转JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON失败: {}", e.getMessage(), e);
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * 对象转格式化的JSON字符串
     *
     * @param obj 对象
     * @return 格式化的JSON字符串
     */
    public static String toJsonPrettyString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转格式化JSON失败: {}", e.getMessage(), e);
            throw new RuntimeException("对象转格式化JSON失败", e);
        }
    }

    /**
     * JSON字符串转对象
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("JSON转对象失败: {}", e.getMessage(), e);
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    /**
     * JSON字符串转复杂类型对象（如List、Map等）
     *
     * @param json          JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("JSON转复杂类型对象失败: {}", e.getMessage(), e);
            throw new RuntimeException("JSON转复杂类型对象失败", e);
        }
    }

    /**
     * 对象转字节数组
     *
     * @param obj 对象
     * @return 字节数组
     */
    public static byte[] toJsonBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转字节数组失败: {}", e.getMessage(), e);
            throw new RuntimeException("对象转字节数组失败", e);
        }
    }

    /**
     * 字节数组转对象
     *
     * @param bytes 字节数组
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 对象
     */
    public static <T> T fromJsonBytes(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("字节数组转对象失败: {}", e.getMessage(), e);
            throw new RuntimeException("字节数组转对象失败", e);
        }
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}

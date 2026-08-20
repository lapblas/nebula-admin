package com.nebula.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Crypto加密工具类，用于密码加密和验证
 */
public class CryptoUtils {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }

}

package com.nebula.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加密工具类，用于前后端密码加密传输
 */
public class RsaUtils {

    /**
     * 解密方法（后端使用）
     * @param encryptedData 加密后的数据（Base64编码）
     * @param privateKeyBase64 私钥（Base64编码）
     * @return 解密后的原始数据
     * @throws Exception 解密异常
     */
    public static String decrypt(String encryptedData, String privateKeyBase64) throws Exception {
        // 解码Base64编码的私钥
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        
        // 解码Base64编码的加密数据
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        // 创建Cipher对象，指定算法为RSA
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // 初始化Cipher对象，设置为解密模式，传入私钥
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // 执行解密操作
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        // 将解密后的字节数组转换为字符串
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 加密方法（前端使用，后端也可用于测试）
     * @param plainData 原始数据
     * @param publicKeyBase64 公钥（Base64编码）
     * @return 加密后的数据（Base64编码）
     * @throws Exception 加密异常
     */
    public static String encrypt(String plainData, String publicKeyBase64) throws Exception {
        // 解码Base64编码的公钥
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        
        // 创建Cipher对象，指定算法为RSA
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // 初始化Cipher对象，设置为加密模式，传入公钥
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 将原始数据转换为字节数组
        byte[] plainBytes = plainData.getBytes(StandardCharsets.UTF_8);
        // 执行加密操作
        byte[] encryptedBytes = cipher.doFinal(plainBytes);
        // 将加密后的字节数组转换为Base64编码的字符串
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 生成 RSA 密钥对
     * @return 密钥对，包含公钥和私钥（Base64编码）
     * @throws Exception 生成异常
     */
    public static Map<String, String> generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Map<String, String> keyMap = new HashMap<>();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        keyMap.put("publicKey", publicKey);
        keyMap.put("privateKey", privateKey);
        return keyMap;
    }
}

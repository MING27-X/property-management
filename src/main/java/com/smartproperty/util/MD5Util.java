package com.smartproperty.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5 加密工具类，用于用户登录密码的加密存储。
 */
public final class MD5Util {

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private MD5Util() {
    }

    /**
     * 对明文进行 MD5 加密，返回 32 位小写十六进制字符串。
     */
    public static String encrypt(String text) {
        if (text == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(HEX[(b >> 4) & 0x0F]).append(HEX[b & 0x0F]);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("MD5 加密失败", e);
        }
    }

    /**
     * 校验明文密码与密文是否匹配。
     */
    public static boolean matches(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }
        return encrypt(rawPassword).equalsIgnoreCase(encryptedPassword);
    }
}

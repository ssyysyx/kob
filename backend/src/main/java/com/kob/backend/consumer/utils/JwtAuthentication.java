package com.kob.backend.consumer.utils;

import com.kob.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthentication {
    // 静态函数不需要用实例来调用
    public static Integer getUserId(String token) {
        int userId = -1;
        try {
            // 解析 JWT，获取其中的用户 ID（subject）这一步一景完成了私钥的验证
            Claims claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            // 解析失败，抛出异常（通常是 JWT 过期或签名错误）
            throw new RuntimeException("JWT 解析失败", e);
        }
        return userId;
    }
}

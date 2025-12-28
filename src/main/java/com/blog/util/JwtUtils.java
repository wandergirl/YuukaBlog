package com.blog.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // 密钥：用于加密和解密，请务必保护好（实际项目可配置在 yaml 中）
    private static final String SECRET_KEY = "YuukaBlog_Secret_Key_2025";

    // 过期时间：设置为 24 小时 (单位：毫秒)
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成 JWT Token
     * @param username 用户名（存入 Payload）
     * @return 字符串令牌
     */
    public static String createToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims) // 设置自定义载荷
                .setSubject(username) // 设置主题
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 使用 HS256 算法签名
                .compact();
    }

    /**
     * 校验并解析 Token
     * @param token 令牌
     * @return 是否合法
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 解析失败（过期、伪造、篡改等情况）
            return false;
        }
    }

    /**
     * 从 Token 中获取用户名
     * @param token 令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
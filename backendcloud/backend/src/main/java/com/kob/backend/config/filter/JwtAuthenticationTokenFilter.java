package com.kob.backend.config.filter;

import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 认证过滤器，每次请求都会执行，用于解析 JWT 并设置认证信息
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserMapper userMapper; // 用于从数据库查询用户信息

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取 Authorization 字段（包含 JWT）
        String token = request.getHeader("Authorization");

        // 检查 token 是否为空，或者是否以 "Bearer " 开头（JWT 通常使用 "Bearer " 前缀）
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // 直接放行请求，进入后续过滤器
            return;
        }

        // 去除 "Bearer " 前缀，获取真正的 JWT
        token = token.substring(7);

        String userid;
        try {
            // 解析 JWT，获取其中的用户 ID（subject）
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            // 解析失败，抛出异常（通常是 JWT 过期或签名错误）
            throw new RuntimeException("JWT 解析失败", e);
        }

        // 通过用户 ID 查询数据库，获取用户信息
        User user = userMapper.selectById(Integer.parseInt(userid));

        // 如果用户不存在，抛出异常（说明该 JWT 无效）
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }

        // 封装用户信息，用于后续 Spring Security 认证
        UserDetailsImpl loginUser = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);

        // 将用户认证信息存入 Spring Security 上下文
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
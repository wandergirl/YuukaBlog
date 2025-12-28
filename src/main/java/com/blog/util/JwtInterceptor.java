package com.blog.util;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行 OPTIONS 请求（处理跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从请求头获取 token
        String token = request.getHeader("token");

        // 3. 校验 Token 是否合法 (这里调用你的 JWT 工具类)
        if (token != null && JwtUtils.validateToken(token)) {
            return true; // 校验通过，放行
        }

        // 4. 校验失败，返回 401 状态码
        response.setStatus(401);
        response.getWriter().write("Invalid Token");
        return false;
    }
}
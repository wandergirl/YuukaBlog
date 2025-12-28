package com.blog.Config;
import com.blog.util.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将所有 /image/** 的网络请求，映射到磁盘的 D:/UploadData/ 目录
        // 注意：路径前缀必须加上 "file:"
        registry.addResourceHandler("/image/*")
                .addResourceLocations("file:/root/upload/");
        registry.addResourceHandler("/avatar/*")
                .addResourceLocations("file:/root/avatar/");
    }
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")             // 拦截所有请求
                .excludePathPatterns("/login")      // 放行登录接口
                .excludePathPatterns("/avatar/**")  // 放行头像接口 (允许上传和查看)
                .excludePathPatterns("/static/**");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
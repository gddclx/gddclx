package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置类
 * 解决前后端分离部署时浏览器的同源策略限制
 * 实现 WebMvcConfigurer 接口，通过 addCorsMappings 方法配置跨域规则
 */
@Configuration  // 标识这是一个Spring配置类，启动时自动加载
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置跨域访问规则
     * @param registry CORS注册表，用于添加跨域映射规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                       // 匹配所有API路径
                .allowedOriginPatterns("*")              // 允许任意域名来源访问（生产环境建议收紧）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP请求方法
                .allowedHeaders("*")                     // 允许携带任意请求头
                .allowCredentials(true)                  // 允许携带Cookie和Authorization等凭证信息
                .maxAge(3600L);                          // 预检请求(OPTIONS)缓存时间：3600秒=1小时
    }
}

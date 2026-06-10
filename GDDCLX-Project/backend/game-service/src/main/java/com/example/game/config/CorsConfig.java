package com.example.game.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 游戏微服务跨域配置
 * 使用 CorsFilter Bean方式（与demo服务的 WebMvcConfigurer 方式不同，但效果一致）
 * 允许所有来源、所有方法、所有请求头
 */
@Configuration
public class CorsConfig {

    @Bean  // 注册为Spring Bean，由Spring容器管理
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");    // 允许所有来源
        config.addAllowedMethod("*");           // 允许所有HTTP方法
        config.addAllowedHeader("*");           // 允许所有请求头
        config.setAllowCredentials(true);       // 允许携带Cookie/Token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 对所有路径生效
        return new CorsFilter(source);
    }
}

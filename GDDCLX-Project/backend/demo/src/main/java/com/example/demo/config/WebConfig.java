package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射配置类
 * 将URL路径 /uploads/** 映射到本地磁盘的物理文件目录
 * 使用户上传的文件可以通过HTTP直接访问
 */
@Configuration  // 标识这是一个Spring配置类
public class WebConfig implements WebMvcConfigurer {

    /**
     * 上传文件存储根目录
     * 从 application.properties 中读取 app.upload.dir 配置项
     * 如果未配置则使用默认值 "uploads"
     * 本地开发: C:/Users/xxx/uploads  服务器: /opt/gddclx/uploads
     */
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * 添加静态资源映射规则
     * 浏览器访问 /uploads/tasks/xxx.docx → 映射到磁盘 file:{uploadDir}/tasks/xxx.docx
     * "file:" 前缀表示这是本地文件系统路径，而不是classpath路径
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")          // URL匹配模式：拦截所有以 /uploads/ 开头的请求
                .addResourceLocations("file:" + uploadDir + "/");  // 映射到本地磁盘的物理目录
    }
}

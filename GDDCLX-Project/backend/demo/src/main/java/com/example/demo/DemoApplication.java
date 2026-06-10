package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * GDDCLX 主微服务启动类 (端口 8080)
 * 负责：考勤管理、任务协作、绩效计算、员工档案、私信、AI助手
 *
 * 注解说明：
 * @SpringBootApplication   = @Configuration + @EnableAutoConfiguration + @ComponentScan
 *                           标识SpringBoot启动类，自动配置并扫描当前包及子包的所有组件
 * @MapperScan              扫描MyBatis Mapper接口，生成代理实现类注入Spring容器
 * @EnableScheduling        启用定时任务支持（用于每天凌晨自动标记未签退员工为早退）
 * @EnableDiscoveryClient   启用Nacos服务发现客户端（生产环境通过JVM参数禁用）
 */
@SpringBootApplication
@MapperScan({"com.example.demo.mapper"})  // 指定Mapper接口所在包路径
@EnableScheduling                          // 开启Spring定时任务 @Scheduled
@EnableDiscoveryClient                     // 注册到Nacos服务中心
public class DemoApplication {
    /**
     * 应用主入口
     * SpringApplication.run() 启动内嵌Tomcat，加载所有Bean，监听8080端口
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

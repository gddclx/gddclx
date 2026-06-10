package com.example.game;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * GDDCLX 游戏微服务启动类 (端口 8081)
 * 负责：金币系统、银行理财、签到、排行榜
 * 独立于主微服务(demo:8080)部署，通过 /api/game/ 路径访问
 *
 * @EnableScheduling 启用定时任务（每天0:00结算投资 + 生成新利率）
 * @EnableDiscoveryClient 注册到Nacos（生产环境通过JVM参数禁用）
 */
@SpringBootApplication
@MapperScan({"com.example.game.mapper"})  // 扫描游戏微服务的Mapper接口
@EnableScheduling
@EnableDiscoveryClient
public class GameServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GameServiceApplication.class, args);
    }
}

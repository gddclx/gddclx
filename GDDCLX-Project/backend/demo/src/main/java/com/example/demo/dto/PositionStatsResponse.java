package com.example.demo.dto;

/**
 * 岗位统计响应DTO — 后端 → 前端
 * GET /api/position/stats 的返回值中的单条记录
 * 用于AI数据接口和前端图表展示各岗位人数分布
 */
public class PositionStatsResponse {
   private String name;  // 岗位名称（如 "前端开发"）
   private int count;    // 该岗位的员工人数

   public String getName() { return this.name; }
   public int getCount() { return this.count; }

   public void setName(String name) { this.name = name; }
   public void setCount(int count) { this.count = count; }

   public PositionStatsResponse() {}

   public PositionStatsResponse(String name, int count) {
      this.name = name;
      this.count = count;
   }
}

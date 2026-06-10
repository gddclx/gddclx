package com.example.demo.dto;

import java.util.List;

/**
 * 岗位分页响应DTO — 后端 → 前端
 * GET /api/position/list?page=1&page_size=10&search=张三 的返回值
 * 包含当前页数据和总条数（供前端分页组件使用）
 */
public class PositionListPageResponse {
   private List<PositionListResponse> list;  // 当前页的岗位数据列表
   private long total;                       // 总记录数（用于计算总页数 = Math.ceil(total / pageSize)）

   public List<PositionListResponse> getList() { return this.list; }
   public long getTotal() { return this.total; }

   public void setList(List<PositionListResponse> list) { this.list = list; }
   public void setTotal(long total) { this.total = total; }

   public PositionListPageResponse() {}

   public PositionListPageResponse(List<PositionListResponse> list, long total) {
      this.list = list;
      this.total = total;
   }
}

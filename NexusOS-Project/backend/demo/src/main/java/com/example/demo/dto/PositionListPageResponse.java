package com.example.demo.dto;

import java.util.List;

public class PositionListPageResponse {
   private List<PositionListResponse> list;
   private long total;

   public List<PositionListResponse> getList() {
      return this.list;
   }

   public long getTotal() {
      return this.total;
   }

   public void setList(List<PositionListResponse> list) {
      this.list = list;
   }

   public void setTotal(long total) {
      this.total = total;
   }


   public PositionListPageResponse() {
   }

   public PositionListPageResponse(List<PositionListResponse> list, long total) {
      this.list = list;
      this.total = total;
   }
}

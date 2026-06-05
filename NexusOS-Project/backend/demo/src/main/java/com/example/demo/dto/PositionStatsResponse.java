package com.example.demo.dto;

public class PositionStatsResponse {
   private String name;
   private int count;

   public String getName() {
      return this.name;
   }

   public int getCount() {
      return this.count;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setCount(int count) {
      this.count = count;
   }


   public PositionStatsResponse() {
   }

   public PositionStatsResponse(String name, int count) {
      this.name = name;
      this.count = count;
   }
}

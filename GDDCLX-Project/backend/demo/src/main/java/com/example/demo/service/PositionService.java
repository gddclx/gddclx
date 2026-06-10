package com.example.demo.service;

import com.example.demo.dto.PositionListPageResponse;
import com.example.demo.dto.PositionStatsResponse;
import com.example.demo.dto.PositionUpdateRequest;
import java.util.List;

public interface PositionService {
   PositionListPageResponse getAllPositionList(int var1, int var2, String var3);

   List<PositionStatsResponse> getPositionStats();

   void updatePositions(PositionUpdateRequest var1);
}

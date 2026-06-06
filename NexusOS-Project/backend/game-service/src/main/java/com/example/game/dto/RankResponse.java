package com.example.game.dto;

import lombok.Data;
import java.util.List;

@Data
public class RankResponse {
    private List<RankItemResponse> top10;
    private RankItemResponse currentUser;
}

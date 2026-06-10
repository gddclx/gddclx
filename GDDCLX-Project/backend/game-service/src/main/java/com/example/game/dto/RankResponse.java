package com.example.game.dto;

import lombok.Data;
import java.util.List;

/** 排行榜响应 — TOP10 + 当前用户排名 */
@Data
public class RankResponse {
    private List<RankItemResponse> top10;       // 前10名
    private RankItemResponse currentUser;        // 当前用户的排名信息
}

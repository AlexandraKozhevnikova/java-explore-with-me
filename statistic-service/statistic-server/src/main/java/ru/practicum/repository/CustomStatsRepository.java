package ru.practicum.repository;

import statisticcommon.HitResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomStatsRepository {
    List<HitResponse> getSummaryHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}

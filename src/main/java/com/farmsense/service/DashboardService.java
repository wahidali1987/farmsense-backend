package com.farmsense.service;

import com.farmsense.dto.DashboardResponse;
import com.farmsense.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository repository;

    public DashboardResponse getDashboard(UUID userId) {

        // 🔹 Summary
        DashboardResponse.Summary summary =
                repository.fetchSummary(userId);

        // 🔹 Batch cards
        List<DashboardResponse.BatchCard> batchCards =
                repository.fetchBatchCards(userId);

        return DashboardResponse.builder()
                .summary(summary)
                .batches(batchCards)
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }
}


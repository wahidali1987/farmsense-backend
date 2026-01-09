package com.farmsense.service;

import com.farmsense.dto.BatchCardProjection;
import com.farmsense.dto.BatchCardResponse;
import com.farmsense.dto.BatchListResponse;
import com.farmsense.dto.PageMetaDto;
import com.farmsense.exception.ResourceNotFoundException;
import com.farmsense.repository.BatchCriteriaRepository;
import com.farmsense.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository repository; // single batch
    private final BatchCriteriaRepository criteriaRepo; // list

    /* ================= SINGLE BATCH (NATIVE) ================= */

    public BatchCardResponse getBatchCard(UUID batchId, UUID userId) {
        Object[] r = repository.fetchBatchCard(batchId, userId);
        if (r == null) {
            throw new ResourceNotFoundException("Batch not found");
        }
        double avgWeight = r[5] == null ? 0 : ((Number) r[5]).doubleValue();
        double expectedWeight = r[6] == null ? 0 : ((Number) r[6]).doubleValue();
        double feedActual = r[7] == null ? 0 : ((Number) r[7]).doubleValue();

        String growthStatus =
                avgWeight == 0 ? "NO_DATA" :
                        avgWeight >= expectedWeight ? "ON_TRACK" : "SLOW";

        String feedStatus =
                feedActual == 0 ? "NO_DATA" :
                        feedActual < expectedWeight * 0.9 ? "LOW_INTAKE" :
                                feedActual > expectedWeight * 1.1 ? "HIGH_INTAKE" : "OK";

        return BatchCardResponse.builder()
                .batchId((UUID) r[0])
                .batchCode((String) r[1])
                .birdType((String) r[2])
                .ageInDays(((Number) r[3]).intValue())
                .status((String) r[4])
                .growth(BatchCardResponse.Growth.builder()
                        .growthStatus(growthStatus).build())
                .feed(BatchCardResponse.Feed.builder()
                        .status(feedStatus).build())
                .health(BatchCardResponse.Health.builder()
                        .riskLevel(r[8] == null ? "LOW" : (String) r[8]).build())
                .profit(
                        BatchCardResponse.Profit.builder()
                                .totalCost(
                                        r[9] == null
                                                ? BigDecimal.ZERO
                                                : BigDecimal.valueOf(((Number) r[9]).doubleValue())
                                )
                                .expectedProfit(
                                        r[10] == null
                                                ? BigDecimal.ZERO
                                                : BigDecimal.valueOf(((Number) r[10]).doubleValue())
                                )
                                .build()
                )

                .build();
    }

    /* ================= LIST BATCHES (CRITERIA) ================= */

    public BatchListResponse getAllBatches(
            UUID userId,
            int page,
            int size,
            String status,
            String birdType,
            String sortBy,
            String sortDir
    ) {

        List<BatchCardProjection> rows =
                criteriaRepo.findBatchCards(
                        userId, status, birdType, sortBy, sortDir, page, size
                );

        long total =
                criteriaRepo.countBatches(userId, status, birdType);

        List<BatchCardResponse> cards =
                rows.stream().map(this::mapProjection).toList();

        return BatchListResponse.builder()
                .success(true)
                .page(new PageMetaDto(
                        page,
                        size,
                        total,
                        (int) Math.ceil((double) total / size)
                ))
                .batches(cards)
                .build();
    }

    /* ================= PROJECTION → RESPONSE ================= */

    private BatchCardResponse mapProjection(BatchCardProjection p) {

        int ageInDays = (int) java.time.temporal.ChronoUnit.DAYS
                .between(p.getStartDate(), LocalDate.now());

        // 🔹 Growth (only weight based)
        String growthStatus =
                p.getAvgWeightG() == null
                        ? "NO_DATA"
                        : "ON_TRACK"; // list me simplified

        // 🔹 Feed (list level pe NA)
        String feedStatus = "NA";

        return BatchCardResponse.builder()
                .batchId(p.getBatchId())
                .batchCode(p.getBatchCode())
                .birdType(p.getBirdType())
                .ageInDays(ageInDays)
                .status(p.getStatus().name())

                .growth(BatchCardResponse.Growth.builder()
                        .growthStatus(growthStatus)
                        .build())

                .feed(BatchCardResponse.Feed.builder()
                        .status(feedStatus)
                        .build())

                .health(BatchCardResponse.Health.builder()
                        .riskLevel(p.getRiskLevel() == null ? "LOW" : p.getRiskLevel())
                        .build())

                .profit(BatchCardResponse.Profit.builder()
                        .expectedProfit(
                                p.getExpectedProfit() == null
                                        ? BigDecimal.ZERO
                                        : p.getExpectedProfit()
                        )
                        .build())
                .build();
    }


}


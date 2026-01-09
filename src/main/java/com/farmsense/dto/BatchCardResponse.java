package com.farmsense.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class BatchCardResponse {

    private UUID batchId;
    private String batchCode;
    private String birdType;
    private int ageInDays;
    private String status;

    private Growth growth;
    private Feed feed;
    private Health health;
    private Profit profit;

    @Data @Builder
    public static class Growth {
        private String growthStatus; // ON_TRACK / SLOW / FAST
    }

    @Data @Builder
    public static class Feed {
        private String status; // OK / LOW_INTAKE / HIGH_INTAKE
    }

    @Data @Builder
    public static class Health {
        private String riskLevel; // LOW / MEDIUM / HIGH
    }

    @Data @Builder
    public static class Profit {
        private BigDecimal totalCost;
        private BigDecimal expectedProfit;
    }
}

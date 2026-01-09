package com.farmsense.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DashboardResponse {

    private Summary summary;
    private List<BatchCard> batches;
    private LocalDateTime lastUpdatedAt;

    /* ---------------- SUMMARY ---------------- */

    @Data
    @Builder
    public static class Summary {
        private int totalBatches;
        private int activeBatches;
        private int totalAliveBirds;
        private String overallRisk; // LOW / MEDIUM / HIGH
        private double todayEstimatedProfit;
    }

    /* ---------------- BATCH CARD ---------------- */

    @Data
    @Builder
    public static class BatchCard {

        private UUID batchId;
        private String batchCode;
        private String birdType;
        private int ageInDays;
        private String status;

        private Birds birds;
        private Growth growth;
        private Feed feed;
        private Health health;
        private Profit profit;
        private Actions actions;
    }

    @Data @Builder
    public static class Birds {
        private int initial;
        private int alive;
        private double mortalityPercent;
    }

    @Data @Builder
    public static class Growth {
        private double avgWeightG;
        private double expectedWeightG;
        private String growthStatus; // ON_TRACK / FAST / SLOW / EARLY_STAGE
        private double deviationPercent;
    }

    @Data @Builder
    public static class Feed {
        private double recommendedPerBirdG;
        private double actualPerBirdG;
        private double deviationPercent;
        private String status; // OK / LOW_INTAKE / HIGH_INTAKE
    }

    @Data @Builder
    public static class Health {
        private String riskLevel;
        private int activeAlerts;
        private String lastAlertMessage;
        private double confidence;
    }

    @Data @Builder
    public static class Profit {
        private double totalCost;
        private double expectedRevenue;
        private double expectedProfit;
        private double costPerBird;
    }

    @Data @Builder
    public static class Actions {
        private boolean viewDetails;
        private String priority; // NORMAL / ATTENTION
    }
}


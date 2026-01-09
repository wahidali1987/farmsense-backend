package com.farmsense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DashboardBatchDto {

    private UUID batchId;
    private String batchCode;
    private String birdType;
    private int ageInDays;
    private String status;

    private int initialBirds;
    private int aliveBirds;
    private BigDecimal mortalityPercent;

    private BigDecimal avgWeightG;
    private BigDecimal expectedWeightG;
    private String growthStatus;

    private BigDecimal feedActualG;
    private BigDecimal feedDeviationPercent;

    private String healthRisk;
    private int activeAlerts;
    private String lastAlertMessage;
    private BigDecimal confidence;

    private BigDecimal totalCost;
    private BigDecimal expectedRevenue;
    private BigDecimal expectedProfit;
    private BigDecimal costPerBird;
}

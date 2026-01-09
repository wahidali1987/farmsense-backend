package com.farmsense.dto;

import com.farmsense.enums.BatchStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class BatchCardProjection {

    private UUID batchId;
    private String batchCode;
    private String birdType;
    private LocalDate startDate;
    private BatchStatus status;
    private BigDecimal avgWeightG;
    private String riskLevel;
    private BigDecimal expectedProfit;

    // 🔑 EXACT constructor Hibernate needs
    public BatchCardProjection(
            UUID batchId,
            String batchCode,
            String birdType,
            LocalDate startDate,
            BatchStatus status,
            BigDecimal avgWeightG,
            String riskLevel,
            BigDecimal expectedProfit
    ) {
        this.batchId = batchId;
        this.batchCode = batchCode;
        this.birdType = birdType;
        this.startDate = startDate;
        this.status = status;
        this.avgWeightG = avgWeightG;
        this.riskLevel = riskLevel;
        this.expectedProfit = expectedProfit;
    }
}

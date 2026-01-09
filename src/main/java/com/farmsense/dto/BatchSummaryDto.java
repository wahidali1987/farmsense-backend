package com.farmsense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BatchSummaryDto {
    private int totalBatches;
    private int activeBatches;
    private int completedBatches;
}


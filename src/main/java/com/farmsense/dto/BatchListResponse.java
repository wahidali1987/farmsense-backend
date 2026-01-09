package com.farmsense.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BatchListResponse {

    private boolean success;
    private BatchSummaryDto summary;
    private PageMetaDto page;
    private List<BatchCardResponse> batches;
}


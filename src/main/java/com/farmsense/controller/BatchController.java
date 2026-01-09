package com.farmsense.controller;

import com.farmsense.dto.BatchCardResponse;
import com.farmsense.dto.BatchListResponse;
import com.farmsense.security.JwtUserPrincipal;
import com.farmsense.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @GetMapping("/{batchId}/card")
    public BatchCardResponse getBatchCard(
            @PathVariable UUID batchId,
            @AuthenticationPrincipal JwtUserPrincipal user
    ) {
        return batchService.getBatchCard(batchId, user.getUserId());
    }

    @GetMapping("/api/batches")
    public BatchListResponse getBatches(
            @AuthenticationPrincipal JwtUserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String birdType,
            @RequestParam(defaultValue = "age") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return batchService.getAllBatches(
                user.getUserId(),
                page,
                size,
                status,
                birdType,
                sortBy,
                sortDir
        );
    }

}

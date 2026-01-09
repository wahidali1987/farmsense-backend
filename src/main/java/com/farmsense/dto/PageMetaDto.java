package com.farmsense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageMetaDto {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}


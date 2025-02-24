package com.example.miniproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchImportResponse {
    private int totalRecords;
    private int successCount;
    private int failureCount;
}

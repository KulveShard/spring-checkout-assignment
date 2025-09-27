package com.assessment.kata.checkoutkata.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResponseDTO {
    private String itemScanned;
    private int currentQuantity;
    private String runningTotal;
    private String message;
}
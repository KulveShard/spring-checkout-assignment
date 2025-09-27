package com.assessment.kata.checkoutkata.dto.checkout;

import com.assessment.kata.checkoutkata.model.DiscountBreakdown;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutSummaryResponseDTO {
    private Map<String, Integer> items;
    private String subtotal;
    private List<DiscountBreakdown> discountsApplied;
    private String totalDiscount;
    private String finalTotal;
}
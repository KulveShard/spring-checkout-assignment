package com.assessment.kata.checkoutkata.dto.pricing;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOfferRequestDTO {

    @Min(value = 2, message = "Offer quantity must be at least 2")
    private int quantity;

    @Min(value = 1, message = "Savings must be positive")
    private int savingsInCents;
}
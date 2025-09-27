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
public class UpdatePriceRequestDTO {

    @Min(value = 1, message = "Price must be at least 1 cent")
    private int newPriceInCents;
}
package com.assessment.kata.checkoutkata.dto.checkout;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemQuantityRequestDTO {

    @Pattern(regexp = "apple|banana|peach|kiwi",
             message = "Item must be one of: apple, banana, peach, kiwi")
    private String itemName;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Builder.Default
    private int quantity = 1;
}
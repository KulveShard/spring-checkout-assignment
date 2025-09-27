package com.assessment.kata.checkoutkata.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentItemsResponseDTO {
    private List<ItemQuantityDTO> items;
}
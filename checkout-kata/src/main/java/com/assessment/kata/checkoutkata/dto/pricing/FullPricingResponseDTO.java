package com.assessment.kata.checkoutkata.dto.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullPricingResponseDTO {
  private String itemKey;
  private int priceInCents;
  private String priceFormatted;
  private Integer offerQuantity;
  private Integer offerSavingsInCents;
  private String offerDescription;
}
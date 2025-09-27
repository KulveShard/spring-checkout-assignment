package com.assessment.kata.checkoutkata.dto.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePricingResponseDTO {
  private String message;
  private String itemName;

  private Integer oldPriceInCents;
  private Integer newPriceInCents;

  private String oldOfferDescription;
  private String newOfferDescription;
}
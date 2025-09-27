package com.assessment.kata.checkoutkata.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscountRule {
  private final int requiredQuantity;
  private final int savingsInCents;
}
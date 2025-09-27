package com.assessment.kata.checkoutkata.model;

import lombok.Data;

@Data
public class DiscountBreakdown {
  private final String itemName;
  private final int discountSets;
  private final int savingsPerSet;
  private final int totalSavings;
}
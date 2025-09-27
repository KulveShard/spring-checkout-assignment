package com.assessment.kata.checkoutkata.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CheckoutSummary {
  private final Map<Item, Integer> items;
  private final int subtotalInCents;
  private final int totalDiscountInCents;
  private final int finalTotalInCents;
  private final List<DiscountBreakdown> appliedDiscounts;
}

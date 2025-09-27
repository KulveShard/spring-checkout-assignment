package com.assessment.kata.checkoutkata.util;

public class CheckoutStringUtils {

  public static String formatPrice(int priceInCents) {
    return String.format("$%.2f", priceInCents / 100.0);
  }
  public static String formatOldOfferDescription(int oldQuantity, int itemPriceInCents, int oldSavingsInCents) {
    return formatOfferDescription(oldQuantity, itemPriceInCents, oldSavingsInCents);
  }

  public static String formatOfferDescription(int quantity, int itemPriceInCents, int savingsInCents) {
    return String.format("%d for %s (save %s)",
        quantity,
        formatPrice(itemPriceInCents * quantity - savingsInCents),
        formatPrice(savingsInCents));
  }
}

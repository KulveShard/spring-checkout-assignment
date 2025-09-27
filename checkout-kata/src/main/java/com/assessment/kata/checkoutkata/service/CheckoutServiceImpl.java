package com.assessment.kata.checkoutkata.service;

import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ItemQuantityRequestDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.event.PriceUpdateEvent;
import com.assessment.kata.checkoutkata.exception.ItemNotFoundException;
import com.assessment.kata.checkoutkata.model.CheckoutSummary;
import com.assessment.kata.checkoutkata.model.DiscountBreakdown;
import com.assessment.kata.checkoutkata.model.DiscountRule;
import com.assessment.kata.checkoutkata.model.Item;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.assessment.kata.checkoutkata.mapper.CheckoutMapper.toCheckoutSummaryResponseDTO;
import static com.assessment.kata.checkoutkata.mapper.CheckoutMapper.toCurrentItemsResponse;
import static com.assessment.kata.checkoutkata.mapper.CheckoutMapper.toRemovalScanResponse;
import static com.assessment.kata.checkoutkata.mapper.CheckoutMapper.toScanResponseDTO;

@Service
public class CheckoutServiceImpl implements CheckoutService {

  private final PricingService pricingService;
  private final Map<Item, Integer> currentCart = new ConcurrentHashMap<>();
  private volatile String lastPriceChangeMessage = null;

  public CheckoutServiceImpl(PricingService pricingService) {
    this.pricingService = pricingService;
  }

  @Override
  public ScanResponseDTO scanItems(ItemQuantityRequestDTO request) throws ItemNotFoundException {
    Item item = Item.fromKey(request.getItemName());
    int quantity = request.getQuantity();

    if (!isValidItem(item)) {
      throw new ItemNotFoundException(String.format("Item %s not found", item));
    }
    currentCart.merge(item, quantity, Integer::sum);
    CheckoutSummary summary = calculateTotal();
    String priceChangeMsg = getAndClearLastPriceChangeMessage();

    return toScanResponseDTO(
        item,
        quantity,
        summary,
        getCurrentItems(),
        priceChangeMsg
    );
  }

  @Override
  public ScanResponseDTO removeItems(ItemQuantityRequestDTO request) throws ItemNotFoundException {
    Item item = Item.fromKey(request.getItemName());
    int quantity = request.getQuantity();
    if (!currentCart.containsKey(item)) {
      throw new ItemNotFoundException(String.format("Item %s not in cart", item));
    }

    currentCart.computeIfPresent(item, (cartItem, currentQuantity) -> {
      int newQuantity = currentQuantity - quantity;
      return newQuantity > 0 ? newQuantity : null;
    });

    CheckoutSummary summary = calculateTotal();

    return toRemovalScanResponse(
        item,
        quantity,
        summary,
        getCurrentItems()
    );
  }

  @Override
  public CheckoutSummaryResponseDTO getCurrentTotal() {
    CheckoutSummary checkoutSummary = calculateTotal();
    return toCheckoutSummaryResponseDTO(checkoutSummary);
  }

  @Override
  public CurrentItemsResponseDTO getCurrentItemsResponse() {
    return toCurrentItemsResponse(getCurrentItems());
  }

  @Override
  public void clearCart() {
    currentCart.clear();
  }

  @EventListener
  public void onPriceUpdate(PriceUpdateEvent event) {
    // Check if this price change affects current cart
    if (currentCart.containsKey(event.getItem())) {

      // Calculate new total with updated prices and create notification
      lastPriceChangeMessage = String.format(
          "🔄 %s Your cart has been recalculated.",
          event.getDescription()
      );
    }
  }

  private String getAndClearLastPriceChangeMessage() {
    String message = lastPriceChangeMessage;
    lastPriceChangeMessage = null;
    return message;
  }

  private boolean isValidItem(Item item) {
    return pricingService.hasItemConfiguration(item);
  }

  private CheckoutSummary calculateTotal() {
    int subtotal = calculateSubtotal();
    int totalDiscount = calculateTotalDiscount();
    int finalTotal = subtotal - totalDiscount;
    List<DiscountBreakdown> breakdowns = calculateDiscountBreakdowns();

    return new CheckoutSummary(
        new HashMap<>(currentCart),
        subtotal,
        totalDiscount,
        finalTotal,
        breakdowns
    );
  }

  private List<DiscountBreakdown> calculateDiscountBreakdowns() {
    return currentCart.entrySet().stream()
        .map(entry -> {
          Item item = entry.getKey();
          int quantity = entry.getValue();
          Optional<DiscountRule> discountRule = pricingService.getDiscountRule(item);

          if (discountRule.isEmpty()) {
            return null;
          }

          int discountSets = quantity / discountRule.get().getRequiredQuantity();
          if (discountSets > 0) {
            return new DiscountBreakdown(
                item.getDisplayName(),
                discountSets,
                discountRule.get().getSavingsInCents(),
                discountSets * discountRule.get().getSavingsInCents()
            );
          }
          return null;
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private int calculateSubtotal() {
    return currentCart.entrySet().stream()
        .mapToInt(entry -> {
          int quantity = entry.getValue();
          int priceInCents = pricingService.getItemPriceInCents(entry.getKey());
          return quantity * priceInCents;
        })
        .sum();
  }

  private int calculateTotalDiscount() {
    return currentCart.entrySet().stream()
        .mapToInt(entry -> {
          Item item = entry.getKey();
          int quantity = entry.getValue();
          Optional<DiscountRule> discountRule = pricingService.getDiscountRule(item);

          if (discountRule.isEmpty()) {
            return 0;
          }

          int discountSets = quantity / discountRule.get().getRequiredQuantity();
          return discountSets * discountRule.get().getSavingsInCents();
        })
        .sum();
  }

  private Map<Item, Integer> getCurrentItems() {
    return new HashMap<>(currentCart);
  }
}

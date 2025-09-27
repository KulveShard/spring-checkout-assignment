package com.assessment.kata.checkoutkata.mapper;

import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ItemQuantityDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.model.CheckoutSummary;
import com.assessment.kata.checkoutkata.model.DiscountBreakdown;
import com.assessment.kata.checkoutkata.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.assessment.kata.checkoutkata.util.CheckoutStringUtils.formatPrice;

public class CheckoutMapper {

    /**
     * Builds the user-facing scan message including discount and price change info.
     */
    public static String buildScanMessage(Item item, int quantity, CheckoutSummary summary, String priceChangeMsg) {
        StringBuilder message = new StringBuilder();

        if (priceChangeMsg != null && !priceChangeMsg.isBlank()) {
            message.append(priceChangeMsg).append(" ");
        }

        Optional<DiscountBreakdown> breakdown = summary.getAppliedDiscounts().stream()
            .filter(d -> d.getItemName().equals(item.getDisplayName()))
            .findFirst();

        if (breakdown.isPresent() && breakdown.get().getDiscountSets() > 0) {
            message.append(String.format("Added %d %s - %s discount applied! Running total: %s",
                quantity,
                item.getDisplayName(),
                breakdown.get().getTotalSavings(),
                formatPrice(summary.getFinalTotalInCents())
            ));
        } else {
            message.append(String.format("Added %d %s - Running total: %s",
                quantity,
                item.getDisplayName(),
                formatPrice(summary.getFinalTotalInCents())
            ));
        }

        return message.toString();
    }

    /**
     * Builds the user-facing removal message for when items are removed from the cart.
     */
    public static String buildRemovalMessage(Item item, int quantityRemoved, CheckoutSummary summary) {
        if (quantityRemoved == 1) {
            return String.format("Removed 1 %s", item.getDisplayName());
        }
        return String.format("Removed %d %s", quantityRemoved, item.getDisplayName());
    }

    /**
     * Maps domain data to a ScanResponseDTO.
     */
    public static ScanResponseDTO toScanResponseDTO(Item item, int quantity, CheckoutSummary summary, Map<Item, Integer> currentItems, String priceChangeMsg) {
        String message = buildScanMessage(item, quantity, summary, priceChangeMsg);
        return new ScanResponseDTO(
            item.getDisplayName(),
            currentItems.getOrDefault(item, 0),
            formatPrice(summary.getFinalTotalInCents()),
            message
        );
    }

    /**
     * Maps domain data to a ScanResponseDTO for removal events.
     */
    public static ScanResponseDTO toRemovalScanResponse(Item item, int quantityRemoved, CheckoutSummary summary, Map<Item, Integer> currentItems) {
        String message = buildRemovalMessage(item, quantityRemoved, summary);
        return new ScanResponseDTO(
            item.getDisplayName(),
            currentItems.getOrDefault(item, 0),
            formatPrice(summary.getFinalTotalInCents()),
            message
        );
    }

    /**
     * Converts internal item map to CurrentItemsResponseDTO with proper structure.
     */
    public static CurrentItemsResponseDTO toCurrentItemsResponse(Map<Item, Integer> items) {
        List<ItemQuantityDTO> itemList = items.entrySet().stream()
            .map(entry -> new ItemQuantityDTO(
                entry.getKey().getDisplayName(),
                entry.getValue()
            ))
            .collect(Collectors.toList());

        return new CurrentItemsResponseDTO(itemList);
    }

    public static CheckoutSummaryResponseDTO toCheckoutSummaryResponseDTO(CheckoutSummary summary) {
        Map<String, Integer> itemsMap = summary.getItems().entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().getDisplayName(),
                Map.Entry::getValue
            ));

        return new CheckoutSummaryResponseDTO(
            itemsMap,
            formatPrice(summary.getSubtotalInCents()),
            summary.getAppliedDiscounts(),
            formatPrice(summary.getTotalDiscountInCents()),
            formatPrice(summary.getFinalTotalInCents())
        );
    }
}

package com.assessment.kata.checkoutkata.cli;

import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ItemQuantityDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.model.Item;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.assessment.kata.checkoutkata.cli.CheckoutConstants.*;
import static com.assessment.kata.checkoutkata.util.CheckoutStringUtils.formatPrice;

@Component
public class MenuDisplayService {

    public void displayStartupMessage() {
        System.out.println(SERVICE_STARTED);
        System.out.println(WEB_API_AVAILABLE);
        System.out.println(CLI_MODE_INSTRUCTION);
        System.out.println(STARTUP_SEPARATOR);
    }

    public void displayWelcomeMessage(Map<String, FullPricingResponseDTO> pricing) {
        System.out.println(DOUBLE_NEWLINE);
        System.out.println(WELCOME_HEADER);
        System.out.println(WELCOME_TITLE);
        System.out.println(WELCOME_HEADER);
        System.out.println();

        displayAvailableItems(pricing);
        displaySpecialOffers(pricing);
        displayTips();
    }

    public void displayMainMenu(CheckoutSummaryResponseDTO summary) {
        System.out.println(NEWLINE + MAIN_MENU_SEPARATOR);
        System.out.println(CURRENT_TOTAL_PREFIX + summary.getFinalTotal());

        if (!EMPTY_TOTAL.equals(summary.getTotalDiscount())) {
            System.out.println(SAVINGS_APPLIED_PREFIX + summary.getTotalDiscount());
        }

        System.out.println(MAIN_MENU_SEPARATOR);
        System.out.println(MENU_SCAN_ITEM);
        System.out.println(MENU_VIEW_TOTAL);
        System.out.println(MENU_VIEW_PRICING);
        System.out.println(MENU_PAY_CLEAR);
        System.out.println(MENU_RESET_CART);
        System.out.println(MENU_ADMIN);
        System.out.println(MENU_EXIT);
    }

    public void displayMainMenuFallback() {
        System.out.println(NEWLINE + MAIN_MENU_SEPARATOR);
        System.out.println(CURRENT_TOTAL_PREFIX + EMPTY_TOTAL);
        System.out.println(MAIN_MENU_SEPARATOR);
        System.out.println(MENU_SCAN_ITEM);
        System.out.println(MENU_VIEW_TOTAL);
        System.out.println(MENU_VIEW_PRICING);
        System.out.println(MENU_PAY_CLEAR);
        System.out.println(MENU_RESET_CART);
        System.out.println(MENU_ADMIN);
        System.out.println(MENU_EXIT);
    }

    public void displayItemSelectionMenu(Map<String, FullPricingResponseDTO> pricing) {
        System.out.println(AVAILABLE_ITEMS_MENU);
        System.out.println(APPLE_OPTION + pricing.get("apple").getPriceFormatted());
        System.out.println(BANANA_OPTION + pricing.get("banana").getPriceFormatted());
        System.out.println(PEACH_OPTION + pricing.get("peach").getPriceFormatted());
        System.out.println(KIWI_OPTION + pricing.get("kiwi").getPriceFormatted());
        System.out.print(ITEM_SELECTION_PROMPT);
    }

    public void displayScanResult(ScanResponseDTO response) {
        if (response.getMessage() != null && !response.getMessage().isEmpty()) {
            System.out.printf(NEWLINE + "%s" + NEWLINE, response.getMessage());
        }

        System.out.printf(ITEM_ADDED_FORMAT, response.getItemScanned());
        System.out.printf(CURRENT_QUANTITY_FORMAT, response.getCurrentQuantity());
        System.out.printf(RUNNING_TOTAL_FORMAT, response.getRunningTotal());
    }

    public void displayCheckoutSummary(CheckoutSummaryResponseDTO summary, CurrentItemsResponseDTO items,
                                      Map<String, FullPricingResponseDTO> pricing) {
        if (items.getItems().isEmpty()) {
            System.out.println(EMPTY_CART);
            return;
        }

        System.out.println(NEWLINE + CHECKOUT_SUMMARY_SEPARATOR);
        System.out.println(CHECKOUT_SUMMARY_HEADER);
        System.out.println(CHECKOUT_SUMMARY_SEPARATOR);

        displayItemsInCart(items, pricing);
        displaySubtotalAndDiscounts(summary);
        displayFinalTotal(summary);
    }

    public void displayCurrentPricing(Map<String, FullPricingResponseDTO> pricing) {
        System.out.println(NEWLINE + CHECKOUT_SUMMARY_SEPARATOR);
        System.out.println("CURRENT PRICING");
        System.out.println(CHECKOUT_SUMMARY_SEPARATOR);

        for (Item item : Item.values()) {
            FullPricingResponseDTO itemPricing = pricing.get(item.getKey());
            if (itemPricing != null) {
                System.out.printf(PRICING_FORMAT, item.getDisplayName(), itemPricing.getPriceFormatted());

                if (itemPricing.getOfferDescription() != null) {
                    System.out.printf(PRICING_WITH_OFFER_FORMAT, itemPricing.getOfferDescription());
                } else {
                    System.out.print(PRICING_NO_OFFER);
                }
            }
        }
        System.out.println(CHECKOUT_SUMMARY_SEPARATOR);
    }

    public void displayPaymentComplete(CheckoutSummaryResponseDTO summary) {
        System.out.println(NEWLINE + PAYMENT_SEPARATOR);
        System.out.println(PAYMENT_COMPLETE);
        System.out.printf(AMOUNT_PAID_PREFIX + "%s" + NEWLINE, summary.getFinalTotal());

        if (!EMPTY_TOTAL.equals(summary.getTotalDiscount())) {
            System.out.printf(YOU_SAVED_PREFIX + "%s" + NEWLINE, summary.getTotalDiscount());
        }

        System.out.println(THANK_YOU_MESSAGE);
        System.out.println(PAYMENT_SEPARATOR);
    }

    public void displayAdminMenu() {
        System.out.println(NEWLINE + PAYMENT_SEPARATOR);
        System.out.println(ADMIN_HEADER);
        System.out.println(PAYMENT_SEPARATOR);

        System.out.println(NEWLINE + ADMIN_VIEW_PRICING);
        System.out.println(ADMIN_UPDATE_PRICE);
        System.out.println(ADMIN_UPDATE_OFFER);
        System.out.println(ADMIN_REMOVE_OFFER);
        System.out.println(ADMIN_BACK);
        System.out.print(ADMIN_PROMPT);
    }

    public void displayAdminItemSelection(String header) {
        System.out.println(header);
        System.out.println(ITEM_CHOICES);
        System.out.print(ITEM_SELECTION_PROMPT);
    }

    public void displayCurrentItemPrice(Item item, FullPricingResponseDTO pricing) {
        System.out.printf(CURRENT_PRICE_FORMAT, item.getDisplayName(), pricing.getPriceFormatted());
    }

    public void displayItemForOffer(Item item, FullPricingResponseDTO pricing) {
        System.out.printf(ITEM_INFO_FORMAT, item.getDisplayName(), pricing.getPriceFormatted());
    }

    public void displayRegularTotal(int quantity, int regularTotal) {
        System.out.printf(REGULAR_TOTAL_FORMAT, quantity, formatPrice(regularTotal));
    }

    public void displaySuccessMessage(String message) {
        System.out.printf(SUCCESS_FORMAT, message);
    }

    public void displayOfferRemoved(String itemName) {
        System.out.printf(OFFER_REMOVED_FORMAT, itemName);
    }

    public void displayErrorMessage(String message) {
        System.err.println("Error: " + message);
    }

    public void displayAdminErrorMessage(String message) {
        System.err.println("Admin Error: " + message);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayPrompt(String prompt) {
        System.out.print(prompt);
    }

    private void displayAvailableItems(Map<String, FullPricingResponseDTO> pricing) {
        System.out.println(AVAILABLE_ITEMS_HEADER);
        for (Item item : Item.values()) {
            FullPricingResponseDTO itemPricing = pricing.get(item.getKey());
            if (itemPricing != null) {
                System.out.printf(ITEM_PRICE_FORMAT,
                    item.getDisplayName(),
                    itemPricing.getPriceFormatted());
            }
        }
        System.out.println();
    }

    private void displaySpecialOffers(Map<String, FullPricingResponseDTO> pricing) {
        System.out.println(SPECIAL_OFFERS_HEADER);
        boolean hasOffers = false;
        for (Item item : Item.values()) {
            FullPricingResponseDTO itemPricing = pricing.get(item.getKey());
            if (itemPricing != null && itemPricing.getOfferDescription() != null) {
                System.out.printf(OFFER_FORMAT, item.getDisplayName(), itemPricing.getOfferDescription());
                hasOffers = true;
            }
        }

        if (!hasOffers) {
            System.out.println(NO_OFFERS_MESSAGE);
        }
    }

    private void displayTips() {
        System.out.println();
        System.out.println(TIPS_HEADER);
        System.out.println(ADMIN_TIP);
        System.out.println(REFRESH_TIP);
        System.out.println(DETACH_TIP);
        System.out.println();
    }

    private void displayItemsInCart(CurrentItemsResponseDTO items, Map<String, FullPricingResponseDTO> pricing) {
        for (ItemQuantityDTO itemDto : items.getItems()) {
            FullPricingResponseDTO itemPricing = pricing.get(itemDto.getItemName());
            if (itemPricing != null) {
                int itemTotal = itemDto.getQuantity() * itemPricing.getPriceInCents();
                System.out.printf(CHECKOUT_ITEM_FORMAT,
                    Item.fromKey(itemDto.getItemName()).getDisplayName(),
                    itemDto.getQuantity(),
                    formatPrice(itemTotal));
            }
        }
    }

    private void displaySubtotalAndDiscounts(CheckoutSummaryResponseDTO summary) {
        System.out.println(SUBTOTAL_SEPARATOR);
        System.out.printf(SUBTOTAL_PREFIX + "%s" + NEWLINE, summary.getSubtotal());

        if (summary.getDiscountsApplied() != null && !summary.getDiscountsApplied().isEmpty()) {
            System.out.println(DISCOUNTS_HEADER);
            summary.getDiscountsApplied().forEach(discount ->
                System.out.printf(DISCOUNT_FORMAT,
                    discount.getItemName(),
                    discount.getTotalSavings()));
        }
    }

    private void displayFinalTotal(CheckoutSummaryResponseDTO summary) {
        System.out.println(SUBTOTAL_SEPARATOR);
        System.out.printf(TOTAL_PREFIX + "%s" + NEWLINE, summary.getFinalTotal());
        System.out.println(CHECKOUT_SUMMARY_SEPARATOR);
    }
}
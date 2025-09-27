package com.assessment.kata.checkoutkata.cli;

import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;
import com.assessment.kata.checkoutkata.model.Item;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.assessment.kata.checkoutkata.cli.CheckoutConstants.*;
import static com.assessment.kata.checkoutkata.util.CheckoutStringUtils.formatPrice;

@Component
public class AdminMenuHandler {

    private final CheckoutApiClient apiClient;
    private final MenuDisplayService displayService;
    private final UserInputHandler inputHandler;

    public AdminMenuHandler(CheckoutApiClient apiClient, MenuDisplayService displayService, UserInputHandler inputHandler) {
        this.apiClient = apiClient;
        this.displayService = displayService;
        this.inputHandler = inputHandler;
    }

    public void runAdminMenu() {
        displayService.displayAdminMenu();

        while (true) {
            displayService.displayAdminMenu();

            int choice = inputHandler.getUserChoice();

            try {
                switch (choice) {
                    case 1:
                        viewCurrentPricing();
                        break;
                    case 2:
                        updateItemPrice();
                        break;
                    case 3:
                        updateItemOffer();
                        break;
                    case 4:
                        removeItemOffer();
                        break;
                    case 5:
                        displayService.displayMessage(RETURNING_TO_CUSTOMER);
                        return;
                    default:
                        displayService.displayMessage(INVALID_ADMIN_OPTION);
                        break;
                }
            } catch (Exception e) {
                displayService.displayAdminErrorMessage(e.getMessage());
            }
        }
    }

    private void viewCurrentPricing() {
        try {
            Map<String, FullPricingResponseDTO> pricing = apiClient.getAllPricing();
            displayService.displayCurrentPricing(pricing);
        } catch (Exception e) {
            displayService.displayErrorMessage("retrieving pricing: " + e.getMessage());
        }
    }

    private void updateItemPrice() {
        try {
            displayService.displayAdminItemSelection(UPDATE_PRICE_HEADER);

            int itemChoice = inputHandler.getUserChoice();
            Item item = validateAndGetItem(itemChoice);
            if (item == null) return;

            Map<String, FullPricingResponseDTO> pricing = apiClient.getAllPricing();
            FullPricingResponseDTO currentPricing = validateAndGetItemPricing(pricing, item);
            if (currentPricing == null) return;

            displayService.displayCurrentItemPrice(item, currentPricing);
            displayService.displayPrompt(NEW_PRICE_PROMPT);

            int newPrice = inputHandler.getUserChoice();
            UserInputHandler.ValidationResult priceValidation = inputHandler.validatePrice(newPrice);
            if (priceValidation != UserInputHandler.ValidationResult.VALID) {
                displayService.displayMessage(inputHandler.getValidationError(priceValidation));
                return;
            }

            UpdatePricingResponseDTO response = apiClient.updatePrice(item.getKey(), newPrice);
            displayService.displaySuccessMessage(response.getMessage());
        } catch (Exception e) {
            displayService.displayErrorMessage("Failed to update price: " + e.getMessage());
        }
    }

    private void updateItemOffer() {
        try {
            displayService.displayAdminItemSelection(UPDATE_OFFER_HEADER);

            int itemChoice = inputHandler.getUserChoice();
            Item item = validateAndGetItem(itemChoice);
            if (item == null) return;

            Map<String, FullPricingResponseDTO> pricing = apiClient.getAllPricing();
            FullPricingResponseDTO currentPricing = validateAndGetItemPricing(pricing, item);
            if (currentPricing == null) return;

            int itemPrice = currentPricing.getPriceInCents();
            displayService.displayItemForOffer(item, currentPricing);

            displayService.displayPrompt(OFFER_QUANTITY_PROMPT);
            int quantity = inputHandler.getUserChoice();
            UserInputHandler.ValidationResult quantityValidation = inputHandler.validateOfferQuantity(quantity);
            if (quantityValidation != UserInputHandler.ValidationResult.VALID) {
                displayService.displayMessage(inputHandler.getValidationError(quantityValidation));
                return;
            }

            int regularTotal = itemPrice * quantity;
            displayService.displayRegularTotal(quantity, regularTotal);

            displayService.displayPrompt(SAVINGS_PROMPT);
            int savingsInCents = inputHandler.getUserChoice();
            UserInputHandler.ValidationResult savingsValidation = inputHandler.validateSavings(savingsInCents, regularTotal);
            if (savingsValidation != UserInputHandler.ValidationResult.VALID) {
                displayService.displayMessage(inputHandler.getValidationError(savingsValidation));
                return;
            }

            UpdatePricingResponseDTO response = apiClient.updateOffer(item.getKey(), quantity, savingsInCents);
            displayService.displaySuccessMessage(response.getMessage());
        } catch (Exception e) {
            displayService.displayErrorMessage("Failed to update offer: " + e.getMessage());
        }
    }

    private void removeItemOffer() {
        try {
            displayService.displayAdminItemSelection(REMOVE_OFFER_HEADER);

            int itemChoice = inputHandler.getUserChoice();
            Item item = validateAndGetItem(itemChoice);
            if (item == null) return;

            Map<String, FullPricingResponseDTO> pricing = apiClient.getAllPricing();
            FullPricingResponseDTO currentPricing = validateAndGetItemPricing(pricing, item);
            if (currentPricing == null) return;

            if (currentPricing.getOfferDescription() == null) {
                displayService.displayMessage(String.format(NO_OFFER_TO_REMOVE_FORMAT, item.getDisplayName()));
                return;
            }

            apiClient.removeOffer(item.getKey());
            displayService.displayOfferRemoved(item.getDisplayName());
        } catch (Exception e) {
            displayService.displayErrorMessage("Failed to remove offer: " + e.getMessage());
        }
    }

    private Item validateAndGetItem(int itemChoice) {
        UserInputHandler.ValidationResult validation = inputHandler.validateItemSelection(itemChoice);
        if (validation != UserInputHandler.ValidationResult.VALID) {
            displayService.displayMessage(inputHandler.getValidationError(validation));
            return null;
        }
        return inputHandler.mapToItem(itemChoice);
    }

    private FullPricingResponseDTO validateAndGetItemPricing(Map<String, FullPricingResponseDTO> pricing, Item item) {
        FullPricingResponseDTO currentPricing = pricing.get(item.getKey());
        if (currentPricing == null) {
            displayService.displayMessage(ITEM_NOT_FOUND_ERROR);
            return null;
        }
        return currentPricing;
    }
}
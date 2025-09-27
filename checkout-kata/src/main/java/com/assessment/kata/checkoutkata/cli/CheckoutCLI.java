package com.assessment.kata.checkoutkata.cli;

import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.model.Item;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.assessment.kata.checkoutkata.cli.CheckoutConstants.*;


@Component
public class CheckoutCLI implements CommandLineRunner {

    private final CheckoutApiClient apiClient;
    private final MenuDisplayService displayService;
    private final UserInputHandler inputHandler;
    private final AdminMenuHandler adminHandler;

    public CheckoutCLI(CheckoutApiClient apiClient, MenuDisplayService displayService,
                      UserInputHandler inputHandler, AdminMenuHandler adminHandler) {
        this.apiClient = apiClient;
        this.displayService = displayService;
        this.inputHandler = inputHandler;
        this.adminHandler = adminHandler;
    }
    @Override
    public void run(String... args) {
        if (args.length > 0 && "cli".equals(args[0])) {
            runInteractiveCLI();
        } else if (args.length == 0) {
            displayService.displayStartupMessage();
        }
    }
    private void runInteractiveCLI() {
        displayWelcomeMessage();

        while (true) {
            displayMainMenu();
            displayService.displayPrompt(MAIN_MENU_PROMPT);

            int choice = inputHandler.getUserChoice();

            try {
                if (inputHandler.isRefreshOption(choice)) {
                    displayWelcomeMessage();
                    continue;
                }

                switch (choice) {
                    case 1:
                        handleScanItem();
                        break;
                    case 2:
                        handleViewTotal();
                        break;
                    case 3:
                        handleViewCurrentPricing();
                        break;
                    case 4:
                        handlePayAndClear();
                        break;
                    case 5:
                        handleResetCart();
                        break;
                    case 6:
                        adminHandler.runAdminMenu();
                        break;
                    case 7:
                        handleExit();
                        break;
                    default:
                        displayService.displayMessage(INVALID_OPTION);
                        break;
                }
            } catch (Exception e) {
                displayService.displayErrorMessage(e.getMessage());
            }
        }
    }
    private void displayWelcomeMessage() {
        try {
            Map<String, FullPricingResponseDTO> pricing = apiClient.getAllPricing();
            displayService.displayWelcomeMessage(pricing);
        } catch (Exception e) {
            displayService.displayErrorMessage("loading pricing information: " + e.getMessage());
        }
    }
    private void displayMainMenu() {
        try {
            CheckoutSummaryResponseDTO summary = apiClient.getCurrentTotal();
            displayService.displayMainMenu(summary);
        } catch (Exception e) {
            displayService.displayMainMenuFallback();
        }
    }
    private void handleScanItem() {
        try {
            Map<String, FullPricingResponseDTO> pricing = apiClient.getAllPricing();
            displayService.displayItemSelectionMenu(pricing);

            int itemChoice = inputHandler.getUserChoice();
            Item item = validateAndGetItem(itemChoice);
            if (item == null) return;

            displayService.displayPrompt(QUANTITY_PROMPT);
            int quantity = inputHandler.getUserChoice();
            if (!validateQuantity(quantity)) return;

            ScanResponseDTO response = apiClient.scanItems(item.getKey(), quantity);
            displayService.displayScanResult(response);
        } catch (Exception e) {
            displayService.displayErrorMessage(e.getMessage());
        }
    }
    private void handleViewTotal() {
        try {
            CheckoutSummaryResponseDTO summary = apiClient.getCurrentTotal();
            CurrentItemsResponseDTO items = apiClient.getCurrentItems();
            Map<String, FullPricingResponseDTO> pricing = apiClient.getAllPricing();

            displayService.displayCheckoutSummary(summary, items, pricing);
        } catch (Exception e) {
            displayService.displayErrorMessage("retrieving checkout summary: " + e.getMessage());
        }
    }
    private void handlePayAndClear() {
        try {
            CheckoutSummaryResponseDTO summary = apiClient.getCurrentTotal();
            CurrentItemsResponseDTO items = apiClient.getCurrentItems();

            if (items.getItems().isEmpty()) {
                displayService.displayMessage(NO_ITEMS_TO_PAY);
                return;
            }

            displayService.displayPaymentComplete(summary);
            apiClient.clearCart();
        } catch (Exception e) {
            displayService.displayErrorMessage("processing payment: " + e.getMessage());
        }
    }
    private void handleResetCart() {
        try {
            apiClient.clearCart();
            displayService.displayMessage(CART_RESET);
        } catch (Exception e) {
            displayService.displayErrorMessage("resetting cart: " + e.getMessage());
        }
    }
    private void handleViewCurrentPricing() {
        try {
            Map<String, FullPricingResponseDTO> pricing = apiClient.getAllPricing();
            displayService.displayCurrentPricing(pricing);
        } catch (Exception e) {
            displayService.displayErrorMessage("retrieving pricing: " + e.getMessage());
        }
    }
    private void handleExit() {
        displayService.displayMessage(EXIT_MESSAGE);
        inputHandler.closeScanner();
        System.exit(0);
    }

    private Item validateAndGetItem(int itemChoice) {
        UserInputHandler.ValidationResult validation = inputHandler.validateItemSelection(itemChoice);
        if (validation != UserInputHandler.ValidationResult.VALID) {
            displayService.displayMessage(inputHandler.getValidationError(validation));
            return null;
        }
        return inputHandler.mapToItem(itemChoice);
    }

    private boolean validateQuantity(int quantity) {
        UserInputHandler.ValidationResult validation = inputHandler.validateQuantity(quantity);
        if (validation != UserInputHandler.ValidationResult.VALID) {
            displayService.displayMessage(inputHandler.getValidationError(validation));
            return false;
        }
        return true;
    }
}
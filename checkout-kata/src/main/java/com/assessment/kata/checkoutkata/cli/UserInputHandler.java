package com.assessment.kata.checkoutkata.cli;

import com.assessment.kata.checkoutkata.model.Item;
import org.springframework.stereotype.Component;

import java.util.Scanner;

import static com.assessment.kata.checkoutkata.cli.CheckoutConstants.*;

@Component
public class UserInputHandler {

    private final Scanner scanner = new Scanner(System.in);

    public int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return INVALID_CHOICE;
        }
    }

    public Item mapToItem(int choice) {
        return switch (choice) {
            case APPLE_CHOICE -> Item.APPLE;
            case BANANA_CHOICE -> Item.BANANA;
            case PEACH_CHOICE -> Item.PEACH;
            case KIWI_CHOICE -> Item.KIWI;
            default -> null;
        };
    }

    public boolean isValidMainMenuChoice(int choice) {
        return choice >= 1 && choice <= 7;
    }

    public boolean isValidAdminMenuChoice(int choice) {
        return choice >= 1 && choice <= 5;
    }

    public boolean isValidItemChoice(int choice) {
        return choice >= APPLE_CHOICE && choice <= KIWI_CHOICE;
    }

    public boolean isPositiveQuantity(int quantity) {
        return quantity > 0;
    }

    public boolean isPositivePrice(int price) {
        return price > 0;
    }

    public boolean isValidOfferQuantity(int quantity) {
        return quantity >= MIN_OFFER_QUANTITY;
    }

    public boolean isValidSavings(int savingsInCents, int regularTotal) {
        return savingsInCents > 0 && savingsInCents < regularTotal;
    }

    public boolean isRefreshOption(int choice) {
        return choice == REFRESH_OPTION;
    }

    public void closeScanner() {
        scanner.close();
    }

    public String getValidationError(ValidationResult result) {
        return switch (result) {
            case INVALID_ITEM_SELECTION -> INVALID_ITEM_SELECTION;
            case POSITIVE_QUANTITY_REQUIRED -> POSITIVE_QUANTITY_ERROR;
            case POSITIVE_PRICE_REQUIRED -> POSITIVE_PRICE_ERROR;
            case MIN_OFFER_QUANTITY_REQUIRED -> MIN_OFFER_QUANTITY_ERROR;
            case INVALID_SAVINGS -> INVALID_SAVINGS_ERROR;
            case INVALID_MAIN_MENU_OPTION -> INVALID_OPTION;
            case INVALID_ADMIN_MENU_OPTION -> INVALID_ADMIN_OPTION;
            default -> "Unknown validation error";
        };
    }

    public ValidationResult validateItemSelection(int choice) {
        if (!isValidItemChoice(choice)) {
            return ValidationResult.INVALID_ITEM_SELECTION;
        }
        return ValidationResult.VALID;
    }

    public ValidationResult validateQuantity(int quantity) {
        if (!isPositiveQuantity(quantity)) {
            return ValidationResult.POSITIVE_QUANTITY_REQUIRED;
        }
        return ValidationResult.VALID;
    }

    public ValidationResult validatePrice(int price) {
        if (!isPositivePrice(price)) {
            return ValidationResult.POSITIVE_PRICE_REQUIRED;
        }
        return ValidationResult.VALID;
    }

    public ValidationResult validateOfferQuantity(int quantity) {
        if (!isValidOfferQuantity(quantity)) {
            return ValidationResult.MIN_OFFER_QUANTITY_REQUIRED;
        }
        return ValidationResult.VALID;
    }

    public ValidationResult validateSavings(int savingsInCents, int regularTotal) {
        if (!isValidSavings(savingsInCents, regularTotal)) {
            return ValidationResult.INVALID_SAVINGS;
        }
        return ValidationResult.VALID;
    }

    public ValidationResult validateMainMenuChoice(int choice) {
        if (!isValidMainMenuChoice(choice)) {
            return ValidationResult.INVALID_MAIN_MENU_OPTION;
        }
        return ValidationResult.VALID;
    }

    public ValidationResult validateAdminMenuChoice(int choice) {
        if (!isValidAdminMenuChoice(choice)) {
            return ValidationResult.INVALID_ADMIN_MENU_OPTION;
        }
        return ValidationResult.VALID;
    }

    public enum ValidationResult {
        VALID,
        INVALID_ITEM_SELECTION,
        POSITIVE_QUANTITY_REQUIRED,
        POSITIVE_PRICE_REQUIRED,
        MIN_OFFER_QUANTITY_REQUIRED,
        INVALID_SAVINGS,
        INVALID_MAIN_MENU_OPTION,
        INVALID_ADMIN_MENU_OPTION
    }
}
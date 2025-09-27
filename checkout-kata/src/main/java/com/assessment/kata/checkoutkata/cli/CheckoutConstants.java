package com.assessment.kata.checkoutkata.cli;

public final class CheckoutConstants {

    private CheckoutConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // API Configuration
    public static final String BASE_URL = "http://localhost:8080";

    // API Endpoints
    public static final String ADMIN_PRICING_ENDPOINT = "/admin/pricing";
    public static final String CHECKOUT_TOTAL_ENDPOINT = "/api/checkout/total";
    public static final String CHECKOUT_ITEMS_ENDPOINT = "/api/checkout/items";
    public static final String CHECKOUT_SCAN_ENDPOINT = "/api/checkout/scan";
    public static final String CHECKOUT_CLEAR_ENDPOINT = "/api/checkout/clear";
    public static final String PRICING_PRICE_ENDPOINT = "/admin/pricing/%s/price";
    public static final String PRICING_OFFER_ENDPOINT = "/admin/pricing/%s/offer";

    // Display Messages
    public static final String WELCOME_HEADER = "🛒 " + "=".repeat(50) + " 🛒";
    public static final String WELCOME_TITLE = "      SUPERMARKET CHECKOUT SYSTEM";
    public static final String AVAILABLE_ITEMS_HEADER = "📦 Available items:";
    public static final String SPECIAL_OFFERS_HEADER = "🎉 Special offers:";
    public static final String NO_OFFERS_MESSAGE = "   • No special offers currently available";
    public static final String TIPS_HEADER = "💡 Tips:";
    public static final String ADMIN_TIP = "   • Use Admin Menu (option 5) to change prices";
    public static final String REFRESH_TIP = "   • Press 0 + Enter anytime to refresh this display";
    public static final String DETACH_TIP = "   • To detach safely: Ctrl+P, then Ctrl+Q";

    // Startup Messages
    public static final String SERVICE_STARTED = "=== Checkout Service Started ===";
    public static final String WEB_API_AVAILABLE = "Web API available at: http://localhost:8080/api/checkout";
    public static final String CLI_MODE_INSTRUCTION = "To run CLI mode, restart with: java -jar app.jar cli";
    public static final String STARTUP_SEPARATOR = "===============================";

    // Menu Options
    public static final String MAIN_MENU_SEPARATOR = "=" + "=".repeat(39);
    public static final String CURRENT_TOTAL_PREFIX = "Current Total: ";
    public static final String SAVINGS_APPLIED_PREFIX = "Savings Applied: ";
    public static final String EMPTY_TOTAL = "$0.00";

    public static final String MENU_SCAN_ITEM = "1. Scan Item";
    public static final String MENU_VIEW_TOTAL = "2. View Detailed Total";
    public static final String MENU_VIEW_PRICING = "3. View Current Pricing";
    public static final String MENU_PAY_CLEAR = "4. Pay & Clear Cart";
    public static final String MENU_RESET_CART = "5. Reset Cart";
    public static final String MENU_ADMIN = "6. Admin Menu";
    public static final String MENU_EXIT = "7. Exit";

    // Item Selection
    public static final String AVAILABLE_ITEMS_MENU = "\nAvailable items:";
    public static final String APPLE_OPTION = "1. Apple - ";
    public static final String BANANA_OPTION = "2. Banana - ";
    public static final String PEACH_OPTION = "3. Peach - ";
    public static final String KIWI_OPTION = "4. Kiwi - ";
    public static final String ITEM_SELECTION_PROMPT = "Select item (1-4): ";
    public static final String QUANTITY_PROMPT = "Enter quantity: ";

    // Admin Menu
    public static final String ADMIN_HEADER = "ADMIN MENU - PRICING MANAGEMENT";
    public static final String ADMIN_VIEW_PRICING = "1. View Current Pricing";
    public static final String ADMIN_UPDATE_PRICE = "2. Update Item Price";
    public static final String ADMIN_UPDATE_OFFER = "3. Update Item Offer";
    public static final String ADMIN_REMOVE_OFFER = "4. Remove Item Offer";
    public static final String ADMIN_BACK = "5. Back to Customer Mode";
    public static final String ADMIN_PROMPT = "Choose admin option (1-5): ";

    // Admin Operations
    public static final String UPDATE_PRICE_HEADER = "\nUpdate Item Price:";
    public static final String UPDATE_OFFER_HEADER = "\nUpdate Item Offer:";
    public static final String REMOVE_OFFER_HEADER = "\nRemove Item Offer:";
    public static final String ITEM_CHOICES = "1. Apple  2. Banana  3. Peach  4. Kiwi";
    public static final String CURRENT_PRICE_FORMAT = "Current price for %s: %s\n";
    public static final String NEW_PRICE_PROMPT = "Enter new price in cents: ";
    public static final String OFFER_QUANTITY_PROMPT = "Enter quantity for offer (e.g., 2 for '2 for X'): ";
    public static final String REGULAR_TOTAL_FORMAT = "Regular total for %d items: %s\n";
    public static final String SAVINGS_PROMPT = "Enter savings in cents: ";

    // Checkout Summary
    public static final String CHECKOUT_SUMMARY_SEPARATOR = "=" + "=".repeat(49);
    public static final String CHECKOUT_SUMMARY_HEADER = "CHECKOUT SUMMARY";
    public static final String SUBTOTAL_SEPARATOR = "-" + "-".repeat(29);
    public static final String SUBTOTAL_PREFIX = "Subtotal: ";
    public static final String DISCOUNTS_HEADER = "\nDiscounts Applied:";
    public static final String TOTAL_PREFIX = "TOTAL: ";

    // Payment
    public static final String PAYMENT_SEPARATOR = "=" + "=".repeat(39);
    public static final String PAYMENT_COMPLETE = "PAYMENT COMPLETE";
    public static final String AMOUNT_PAID_PREFIX = "Amount paid: ";
    public static final String YOU_SAVED_PREFIX = "You saved: ";
    public static final String THANK_YOU_MESSAGE = "Thank you for your purchase!";

    // Success Messages
    public static final String ITEM_ADDED_FORMAT = "\n✓ Added to cart: %s\n";
    public static final String CURRENT_QUANTITY_FORMAT = "Current quantity: %d\n";
    public static final String RUNNING_TOTAL_FORMAT = "Running total: %s\n";
    public static final String SUCCESS_FORMAT = "✅ %s\n";
    public static final String OFFER_REMOVED_FORMAT = "✅ Removed offer for %s\n";

    // Error Messages
    public static final String INVALID_OPTION = "Invalid option! Please choose 1-7.";
    public static final String INVALID_ADMIN_OPTION = "Invalid option! Please choose 1-5.";
    public static final String INVALID_ITEM_SELECTION = "Invalid item selection!";
    public static final String POSITIVE_QUANTITY_ERROR = "Quantity must be positive!";
    public static final String POSITIVE_PRICE_ERROR = "Price must be positive!";
    public static final String MIN_OFFER_QUANTITY_ERROR = "Offer quantity must be at least 2!";
    public static final String INVALID_SAVINGS_ERROR = "Savings must be positive and less than regular total!";
    public static final String ITEM_NOT_FOUND_ERROR = "Item not found in pricing!";
    public static final String NO_OFFER_TO_REMOVE_FORMAT = "%s has no offer to remove.\n";

    // Status Messages
    public static final String EMPTY_CART = "\nYour cart is empty.";
    public static final String NO_ITEMS_TO_PAY = "\nNo items to pay for.";
    public static final String CART_RESET = "\nCart has been reset.";
    public static final String RETURNING_TO_CUSTOMER = "Returning to customer mode...";
    public static final String EXIT_MESSAGE = "\nThank you for using the checkout system!";

    // Prompts
    public static final String MAIN_MENU_PROMPT = "Choose option (1-7): ";

    // Formatting
    public static final String ITEM_PRICE_FORMAT = "   • %-6s - %s each%n";
    public static final String OFFER_FORMAT = "   • %s: %s%n";
    public static final String CHECKOUT_ITEM_FORMAT = "%-10s x%d  %s\n";
    public static final String DISCOUNT_FORMAT = "  %s: -%s\n";
    public static final String PRICING_FORMAT = "%-10s: %s";
    public static final String PRICING_WITH_OFFER_FORMAT = " | %s\n";
    public static final String PRICING_NO_OFFER = " | No offer\n";
    public static final String ITEM_INFO_FORMAT = "Item: %s (%s each)\n";

    // Separators and Formatting
    public static final String DOUBLE_NEWLINE = "\n\n";
    public static final String NEWLINE = "\n";

    // Magic Numbers
    public static final int MIN_OFFER_QUANTITY = 2;
    public static final int INVALID_CHOICE = -1;
    public static final int REFRESH_OPTION = 0;
    public static final int APPLE_CHOICE = 1;
    public static final int BANANA_CHOICE = 2;
    public static final int PEACH_CHOICE = 3;
    public static final int KIWI_CHOICE = 4;
}
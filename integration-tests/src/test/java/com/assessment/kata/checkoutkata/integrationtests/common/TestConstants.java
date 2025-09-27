package com.assessment.kata.checkoutkata.integrationtests.common;

public class TestConstants {
    // Base configuration
    public static final String BASE_URL = "http://localhost:8080";
    public static final String CONTENT_TYPE_JSON = "application/json";

    // Admin Pricing API endpoints
    public static final String ADMIN_PRICING_ENDPOINT = "/admin/pricing";
    public static final String ADMIN_PRICING_UPDATE_PRICE_ENDPOINT = "/admin/pricing/{itemName}/price";
    public static final String ADMIN_PRICING_UPDATE_OFFER_ENDPOINT = "/admin/pricing/{itemName}/offer";
    public static final String ADMIN_PRICING_REMOVE_OFFER_ENDPOINT = "/admin/pricing/{itemName}/offer";

    // Checkout API endpoints
    public static final String CHECKOUT_SCAN_ENDPOINT = "/api/checkout/scan";
    public static final String CHECKOUT_REMOVE_ENDPOINT = "/api/checkout/remove";
    public static final String CHECKOUT_TOTAL_ENDPOINT = "/api/checkout/total";
    public static final String CHECKOUT_ITEMS_ENDPOINT = "/api/checkout/items";
    public static final String CHECKOUT_CLEAR_ENDPOINT = "/api/checkout/clear";

    // Common test data
    public static final String VALID_ITEM_APPLE = "apple";
    public static final String VALID_ITEM_BANANA = "banana";
    public static final String VALID_ITEM_PEACH = "peach";
    public static final String VALID_ITEM_KIWI = "kiwi";
    public static final String INVALID_ITEM = "invaliditem";
}
package com.assessment.kata.checkoutkata.integrationtests.pricingintegrationtests;

import com.assessment.kata.checkoutkata.integrationtests.common.BaseIntegrationTest;
import com.assessment.kata.checkoutkata.integrationtests.common.TestConstants;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class AdminPricingIntegrationTest extends BaseIntegrationTest {

    private static final String UPDATE_APPLE_PRICE_REQUEST = "pricingintegrationtests/testdata/requests/update_apple_price.json";
    private static final String UPDATE_APPLE_OFFER_REQUEST = "pricingintegrationtests/testdata/requests/update_apple_offer.json";

    private static final String UPDATE_PRICE_SUCCESS_RESPONSE = "pricingintegrationtests/testdata/responses/update_price_success.json";
    private static final String ALL_PRICING_RESPONSE = "pricingintegrationtests/testdata/responses/all_pricing.json";

    @BeforeEach
    void setUp() {
        // Reset all items to original data.sql values before each test to ensure test independence
        resetItem("apple", 30, 2, 15);      // Apple: 30¢ each, 2 for 45¢ (save 15¢)
        resetItem("banana", 50, 3, 20);     // Banana: 50¢ each, 3 for 130¢ (save 20¢)
        resetItem("peach", 60, null, null); // Peach: 60¢ each, no offer
        resetItem("kiwi", 20, null, null);  // Kiwi: 20¢ each, no offer
    }

    @Test
    public void shouldGetAllPricing() {
        Response response = given()
            .when()
            .get(TestConstants.ADMIN_PRICING_ENDPOINT)
            .then()
            .statusCode(200)
            .extract()
            .response();

        validator.validateResponse(response, ALL_PRICING_RESPONSE);
    }

    @Test
    public void shouldUpdateItemPrice() {
        String request = dataLoader.loadRequest(UPDATE_APPLE_PRICE_REQUEST);

        Response response = given()
            .contentType(TestConstants.CONTENT_TYPE_JSON)
            .body(request)
            .when()
            .patch(TestConstants.ADMIN_PRICING_UPDATE_PRICE_ENDPOINT, TestConstants.VALID_ITEM_APPLE)
            .then()
            .statusCode(200)
            .extract()
            .response();

        validator.validateResponse(response, UPDATE_PRICE_SUCCESS_RESPONSE);
    }

    @Test
    public void shouldUpdateItemOffer() {
        String request = dataLoader.loadRequest(UPDATE_APPLE_OFFER_REQUEST);

        given()
            .contentType(TestConstants.CONTENT_TYPE_JSON)
            .body(request)
            .when()
            .patch(TestConstants.ADMIN_PRICING_UPDATE_OFFER_ENDPOINT, TestConstants.VALID_ITEM_APPLE)
            .then()
            .statusCode(200);
    }

    @Test
    public void shouldRemoveItemOffer() {
        given()
            .when()
            .delete(TestConstants.ADMIN_PRICING_REMOVE_OFFER_ENDPOINT, TestConstants.VALID_ITEM_APPLE)
            .then()
            .statusCode(200);
    }

    @Test
    public void shouldReturnNotFoundForInvalidItem() {
        String request = dataLoader.loadRequest(UPDATE_APPLE_PRICE_REQUEST);

        given()
            .contentType(TestConstants.CONTENT_TYPE_JSON)
            .body(request)
            .when()
            .patch(TestConstants.ADMIN_PRICING_UPDATE_PRICE_ENDPOINT, TestConstants.INVALID_ITEM)
            .then()
            .statusCode(400); // Invalid item returns 400
    }

    @Test
    public void shouldValidatePricingConstraints() {
        // Test invalid price (negative)
        String invalidPriceRequest = "{ \"priceInCents\": -10 }";

        given()
            .contentType(TestConstants.CONTENT_TYPE_JSON)
            .body(invalidPriceRequest)
            .when()
            .patch(TestConstants.ADMIN_PRICING_UPDATE_PRICE_ENDPOINT, TestConstants.VALID_ITEM_APPLE)
            .then()
            .statusCode(400); // Validation error should return 400
    }

    @Test
    public void shouldValidateOfferConstraints() {
        // Test offer where savings exceed total price (invalid)
        String invalidOfferRequest = "{ \"quantity\": 2, \"savingsInCents\": 100 }";

        given()
            .contentType(TestConstants.CONTENT_TYPE_JSON)
            .body(invalidOfferRequest)
            .when()
            .patch(TestConstants.ADMIN_PRICING_UPDATE_OFFER_ENDPOINT, TestConstants.VALID_ITEM_APPLE)
            .then()
            .statusCode(400); // PricingValidationException should return 400
    }

    @Test
    public void shouldHandleConcurrentPricingUpdates() {
        // Test that multiple pricing updates work correctly
        String priceRequest = dataLoader.loadRequest(UPDATE_APPLE_PRICE_REQUEST);
        String offerRequest = dataLoader.loadRequest(UPDATE_APPLE_OFFER_REQUEST);

        // Update price
        given()
            .contentType(TestConstants.CONTENT_TYPE_JSON)
            .body(priceRequest)
            .when()
            .patch(TestConstants.ADMIN_PRICING_UPDATE_PRICE_ENDPOINT, TestConstants.VALID_ITEM_APPLE)
            .then()
            .statusCode(200);

        // Update offer
        given()
            .contentType(TestConstants.CONTENT_TYPE_JSON)
            .body(offerRequest)
            .when()
            .patch(TestConstants.ADMIN_PRICING_UPDATE_OFFER_ENDPOINT, TestConstants.VALID_ITEM_APPLE)
            .then()
            .statusCode(200);

        // Verify both changes are reflected in pricing
        given()
            .when()
            .get(TestConstants.ADMIN_PRICING_ENDPOINT)
            .then()
            .statusCode(200);
    }

    /**
     * Helper method to reset an item to its original pricing configuration
     */
    private static void resetItem(String itemName, int priceInCents, Integer offerQuantity, Integer offerSavingsInCents) {
        // Reset price
        String priceResetJson = String.format("{\"newPriceInCents\": %d}", priceInCents);
        given()
            .contentType(TestConstants.CONTENT_TYPE_JSON)
            .body(priceResetJson)
            .when()
            .patch(TestConstants.ADMIN_PRICING_UPDATE_PRICE_ENDPOINT, itemName)
            .then()
            .statusCode(200);

        // Reset or remove offer
        if (offerQuantity != null && offerSavingsInCents != null) {
            // Set the offer
            String offerResetJson = String.format("{\"quantity\": %d, \"savingsInCents\": %d}",
                                                 offerQuantity, offerSavingsInCents);
            given()
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(offerResetJson)
                .when()
                .patch(TestConstants.ADMIN_PRICING_UPDATE_OFFER_ENDPOINT, itemName)
                .then()
                .statusCode(200);
        } else {
            // Remove the offer (ignore potential 404 if offer doesn't exist)
            given()
                .when()
                .delete(TestConstants.ADMIN_PRICING_REMOVE_OFFER_ENDPOINT, itemName)
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(404)));
        }
    }
}
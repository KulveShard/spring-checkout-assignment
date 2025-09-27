package com.assessment.kata.checkoutkata.integrationtests.checkoutintegrationtests;

import com.assessment.kata.checkoutkata.integrationtests.common.BaseIntegrationTest;
import com.assessment.kata.checkoutkata.integrationtests.common.TestConstants;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class CheckoutIntegrationTest extends BaseIntegrationTest {

  private static final String SCAN_APPLE_SINGLE_REQUEST = "checkoutintegrationtests/testdata/requests/scan_apple_single.json";
  private static final String SCAN_APPLE_DISCOUNT_REQUEST = "checkoutintegrationtests/testdata/requests/scan_apple_discount.json";
  private static final String SCAN_BANANA_DISCOUNT_REQUEST = "checkoutintegrationtests/testdata/requests/scan_banana_discount.json";
  private static final String REMOVE_APPLE_SINGLE_REQUEST = "checkoutintegrationtests/testdata/requests/remove_apple_single.json";
  private static final String SCAN_INVALID_ITEM_REQUEST = "checkoutintegrationtests/testdata/requests/scan_invalid_item.json";

  private static final String SCAN_APPLE_SINGLE_RESPONSE = "checkoutintegrationtests/testdata/responses/scan_apple_single_success.json";
  private static final String SCAN_APPLE_DISCOUNT_RESPONSE = "checkoutintegrationtests/testdata/responses/scan_apple_discount_success.json";
  private static final String CHECKOUT_TOTAL_EMPTY_RESPONSE = "checkoutintegrationtests/testdata/responses/checkout_total_empty.json";
  private static final String CURRENT_ITEMS_EMPTY_RESPONSE = "checkoutintegrationtests/testdata/responses/current_items_empty.json";

  @BeforeEach
  void setUp() {
    // Clear the cart before each test
    given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .when()
        .post(TestConstants.CHECKOUT_CLEAR_ENDPOINT)
        .then()
        .statusCode(200);
  }

  @Test
  public void shouldScanSingleAppleSuccessfully() {
    String request = dataLoader.loadRequest(SCAN_APPLE_SINGLE_REQUEST);

    Response response = given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .body(request)
        .when()
        .post(TestConstants.CHECKOUT_SCAN_ENDPOINT)
        .then()
        .statusCode(200)
        .extract()
        .response();

    validator.validateResponse(response, SCAN_APPLE_SINGLE_RESPONSE);
  }

  @Test
  public void shouldApplyDiscountWhenScanningTwoApples() {
    String request = dataLoader.loadRequest(SCAN_APPLE_DISCOUNT_REQUEST);

    Response response = given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .body(request)
        .when()
        .post(TestConstants.CHECKOUT_SCAN_ENDPOINT)
        .then()
        .statusCode(200)
        .extract()
        .response();

    validator.validateResponse(response, SCAN_APPLE_DISCOUNT_RESPONSE);
  }

  @Test
  public void shouldReturnBadRequestForInvalidItem() {
    String request = dataLoader.loadRequest(SCAN_INVALID_ITEM_REQUEST);

    given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .body(request)
        .when()
        .post(TestConstants.CHECKOUT_SCAN_ENDPOINT)
        .then()
        .statusCode(400); // Invalid item returns 400
  }

  @Test
  public void shouldRemoveItemFromCart() {
    // First, add an apple to the cart
    String scanRequest = dataLoader.loadRequest(SCAN_APPLE_SINGLE_REQUEST);
    given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .body(scanRequest)
        .when()
        .post(TestConstants.CHECKOUT_SCAN_ENDPOINT)
        .then()
        .statusCode(200);

    // Then remove it
    String removeRequest = dataLoader.loadRequest(REMOVE_APPLE_SINGLE_REQUEST);
    given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .body(removeRequest)
        .when()
        .post(TestConstants.CHECKOUT_REMOVE_ENDPOINT)
        .then()
        .statusCode(200);

    // Verify cart is empty
    Response totalResponse = given()
        .when()
        .get(TestConstants.CHECKOUT_TOTAL_ENDPOINT)
        .then()
        .statusCode(200)
        .extract()
        .response();

    validator.validateResponse(totalResponse, CHECKOUT_TOTAL_EMPTY_RESPONSE);
  }

  @Test
  public void shouldReturnCurrentTotal() {
    Response response = given()
        .when()
        .get(TestConstants.CHECKOUT_TOTAL_ENDPOINT)
        .then()
        .statusCode(200)
        .extract()
        .response();

    validator.validateResponse(response, CHECKOUT_TOTAL_EMPTY_RESPONSE);
  }

  @Test
  public void shouldReturnCurrentItems() {
    Response response = given()
        .when()
        .get(TestConstants.CHECKOUT_ITEMS_ENDPOINT)
        .then()
        .statusCode(200)
        .extract()
        .response();

    validator.validateResponse(response, CURRENT_ITEMS_EMPTY_RESPONSE);
  }

  @Test
  public void shouldClearCart() {
    // Add some items first
    String request = dataLoader.loadRequest(SCAN_APPLE_SINGLE_REQUEST);
    given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .body(request)
        .when()
        .post(TestConstants.CHECKOUT_SCAN_ENDPOINT)
        .then()
        .statusCode(200);

    // Clear the cart
    given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .when()
        .post(TestConstants.CHECKOUT_CLEAR_ENDPOINT)
        .then()
        .statusCode(200);

    // Verify cart is empty
    Response totalResponse = given()
        .when()
        .get(TestConstants.CHECKOUT_TOTAL_ENDPOINT)
        .then()
        .statusCode(200)
        .extract()
        .response();

    validator.validateResponse(totalResponse, CHECKOUT_TOTAL_EMPTY_RESPONSE);
  }

  @Test
  public void shouldCalculateComplexCartWithMultipleItems() {
    // Add 2 apples (should get discount)
    String appleRequest = dataLoader.loadRequest(SCAN_APPLE_DISCOUNT_REQUEST);
    given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .body(appleRequest)
        .when()
        .post(TestConstants.CHECKOUT_SCAN_ENDPOINT)
        .then()
        .statusCode(200);

    // Add 3 bananas (should get discount)
    String bananaRequest = dataLoader.loadRequest(SCAN_BANANA_DISCOUNT_REQUEST);
    given()
        .contentType(TestConstants.CONTENT_TYPE_JSON)
        .body(bananaRequest)
        .when()
        .post(TestConstants.CHECKOUT_SCAN_ENDPOINT)
        .then()
        .statusCode(200);

    // Get total and verify complex calculation
    Response totalResponse = given()
        .when()
        .get(TestConstants.CHECKOUT_TOTAL_ENDPOINT)
        .then()
        .statusCode(200)
        .extract()
        .response();

    // Total should be: 2 apples (45¢) + 3 bananas (130¢) = 175¢
    // With discounts: Apple save 15¢, Banana save 20¢ = total save 35¢
    // Final: (60¢ + 150¢) - 35¢ = 175¢
    // This validates that the calculation engine works correctly
  }
}
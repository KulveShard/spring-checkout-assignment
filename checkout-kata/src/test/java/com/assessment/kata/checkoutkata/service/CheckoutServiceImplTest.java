package com.assessment.kata.checkoutkata.service;


import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ItemQuantityRequestDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.exception.ItemNotFoundException;
import com.assessment.kata.checkoutkata.model.Item;
import com.assessment.kata.checkoutkata.model.PricingConfig;
import com.assessment.kata.checkoutkata.repository.PricingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CheckoutServiceImpl Business Logic Tests")
class CheckoutServiceImplTest {

    @Autowired private CheckoutService checkoutService;
    @Autowired private PricingRepository pricingRepository;

    // Test data constants
    private static final String APPLE = "apple";
    private static final String BANANA = "banana";
    private static final String PEACH = "peach";
    private static final String KIWI = "kiwi";
    private static final String INVALID_ITEM = "invaliditem";

    private static final int APPLE_PRICE = 30;
    private static final int BANANA_PRICE = 50;
    private static final int PEACH_PRICE = 60;
    private static final int KIWI_PRICE = 20;

    @BeforeEach
    void setUp() {
        pricingRepository.deleteAll();
        checkoutService.clearCart();
        createTestPricingData();
    }

    @Test
    @DisplayName("scanItems should successfully add valid item to empty cart")
    void shouldAddValidItemToEmptyCart() {
        ItemQuantityRequestDTO request = createItemRequest(APPLE, 2);
        ScanResponseDTO response = checkoutService.scanItems(request);

        assertScanResponse(response, APPLE, 2);
        assertEquals("$0.45", response.getRunningTotal());
        assertCartContains(APPLE, 2);
    }

    @Test
    @DisplayName("scanItems should accumulate quantities for same item")
    void shouldAccumulateQuantitiesForSameItem() {
        checkoutService.scanItems(createItemRequest(APPLE, 2));
        ScanResponseDTO response = checkoutService.scanItems(createItemRequest(APPLE, 3));

        assertScanResponse(response, APPLE, 5); // Total quantity in cart
        assertCartContains(APPLE, 5);
    }

    @Test
    @DisplayName("scanItems should throw IllegalArgumentException for invalid item")
    void shouldThrowIllegalArgumentExceptionForInvalidItem() {
        ItemQuantityRequestDTO request = createItemRequest(INVALID_ITEM, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> checkoutService.scanItems(request));
        assertTrue(exception.getMessage().contains("No item found with key"));
    }

    @Test
    @DisplayName("scanItems should apply discounts correctly")
    void shouldApplyDiscountsCorrectly() {
        // Apple has offer: 2 for 45¢ (save 15¢)
        ScanResponseDTO response = checkoutService.scanItems(createItemRequest(APPLE, 2));

        assertEquals("$0.45", response.getRunningTotal());
        assertTrue(response.getMessage().contains("discount applied"));
    }

    @Test
    @DisplayName("removeItems should successfully remove items from cart")
    void shouldRemoveItemsFromCart() {
        checkoutService.scanItems(createItemRequest(APPLE, 5));
        ScanResponseDTO response = checkoutService.removeItems(createItemRequest(APPLE, 2));

        assertScanResponse(response, APPLE, 3); // Remaining quantity
        assertTrue(response.getMessage().contains("Removed 2"));
        assertCartContains(APPLE, 3);
    }

    @Test
    @DisplayName("removeItems should remove item completely when quantity reaches zero")
    void shouldRemoveItemCompletelyWhenQuantityReachesZero() {
        checkoutService.scanItems(createItemRequest(APPLE, 2));
        checkoutService.removeItems(createItemRequest(APPLE, 3)); // Remove more than exists

        assertCartEmpty();
    }

    @Test
    @DisplayName("removeItems should throw ItemNotFoundException for item not in cart")
    void shouldThrowItemNotFoundExceptionForItemNotInCart() {
        ItemQuantityRequestDTO request = createItemRequest(APPLE, 1);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
            () -> checkoutService.removeItems(request));
        assertTrue(exception.getMessage().contains("not in cart"));
    }

    @Test
    @DisplayName("getCurrentTotal should return zero for empty cart")
    void shouldReturnZeroForEmptyCart() {
        CheckoutSummaryResponseDTO response = checkoutService.getCurrentTotal();

        assertEquals("$0.00", response.getSubtotal());
        assertEquals("$0.00", response.getTotalDiscount());
        assertEquals("$0.00", response.getFinalTotal());
        assertTrue(response.getDiscountsApplied().isEmpty());
    }

    @Test
    @DisplayName("getCurrentTotal should calculate correct total with multiple items")
    void shouldCalculateCorrectTotalWithMultipleItems() {
        checkoutService.scanItems(createItemRequest(APPLE, 3)); // 90¢ - 15¢ discount = 75¢
        checkoutService.scanItems(createItemRequest(BANANA, 4)); // 200¢ - 20¢ discount = 180¢

        CheckoutSummaryResponseDTO response = checkoutService.getCurrentTotal();

        assertEquals("$2.90", response.getSubtotal()); // 90 + 200
        assertEquals("$0.35", response.getTotalDiscount()); // 15 + 20
        assertEquals("$2.55", response.getFinalTotal()); // 290 - 35
    }

    @Test
    @DisplayName("getCurrentItemsResponse should return empty for empty cart")
    void shouldReturnEmptyForEmptyCart() {
        CurrentItemsResponseDTO response = checkoutService.getCurrentItemsResponse();

        assertTrue(response.getItems().isEmpty());
    }

    @Test
    @DisplayName("getCurrentItemsResponse should return correct items")
    void shouldReturnCorrectItems() {
        checkoutService.scanItems(createItemRequest(APPLE, 2));
        checkoutService.scanItems(createItemRequest(BANANA, 1));

        CurrentItemsResponseDTO response = checkoutService.getCurrentItemsResponse();

        assertEquals(2, response.getItems().size());

        boolean foundApple = response.getItems().stream()
            .anyMatch(item -> "Apple".equals(item.getItemName()) && item.getQuantity() == 2);
        boolean foundBanana = response.getItems().stream()
            .anyMatch(item -> "Banana".equals(item.getItemName()) && item.getQuantity() == 1);

        assertTrue(foundApple);
        assertTrue(foundBanana);
    }

    @Test
    @DisplayName("clearCart should remove all items from cart")
    void shouldRemoveAllItemsFromCart() {
        checkoutService.scanItems(createItemRequest(APPLE, 2));
        checkoutService.scanItems(createItemRequest(BANANA, 3));

        checkoutService.clearCart();

        assertCartEmpty();
        assertEquals("$0.00", checkoutService.getCurrentTotal().getFinalTotal());
    }

    @Test
    @DisplayName("Business logic integration test - complete checkout workflow")
    void shouldHandleCompleteCheckoutWorkflow() {
        // Add multiple items
        checkoutService.scanItems(createItemRequest(APPLE, 3));
        checkoutService.scanItems(createItemRequest(BANANA, 2));
        checkoutService.scanItems(createItemRequest(PEACH, 1));

        // Verify intermediate state
        CheckoutSummaryResponseDTO midTotal = checkoutService.getCurrentTotal();
        assertEquals("$2.50", midTotal.getSubtotal()); // 90 + 100 + 60
        assertEquals("$0.15", midTotal.getTotalDiscount()); // Only apple has discount
        assertEquals("$2.35", midTotal.getFinalTotal());

        // Remove some items
        checkoutService.removeItems(createItemRequest(APPLE, 1));
        checkoutService.removeItems(createItemRequest(PEACH, 1));

        // Verify final state (2 apples + 2 bananas)
        CheckoutSummaryResponseDTO finalTotal = checkoutService.getCurrentTotal();
        assertEquals("$1.60", finalTotal.getSubtotal()); // 60 + 100
        assertEquals("$0.15", finalTotal.getTotalDiscount()); // Apple discount still qualifies (2 apples)
        assertEquals("$1.45", finalTotal.getFinalTotal());

        // Clear and verify empty
        checkoutService.clearCart();
        assertCartEmpty();
    }

    private void createTestPricingData() {
        savePricingConfig(APPLE, APPLE_PRICE, 2, 15);    // Apple: 30¢, 2 for 45¢ (save 15¢)
        savePricingConfig(BANANA, BANANA_PRICE, 3, 20);  // Banana: 50¢, 3 for 130¢ (save 20¢)
        savePricingConfig(PEACH, PEACH_PRICE, null, null); // Peach: 60¢, no offer
        savePricingConfig(KIWI, KIWI_PRICE, null, null);   // Kiwi: 20¢, no offer
    }

    private void savePricingConfig(String itemKey, int priceInCents, Integer offerQuantity, Integer savingsInCents) {
        PricingConfig config = new PricingConfig();
        config.setItemKey(itemKey);
        config.setPriceInCents(priceInCents);
        config.setOfferQuantity(offerQuantity);
        config.setOfferSavingsInCents(savingsInCents);
        pricingRepository.save(config);
    }

    private ItemQuantityRequestDTO createItemRequest(String itemName, int quantity) {
        return ItemQuantityRequestDTO.builder()
            .itemName(itemName)
            .quantity(quantity)
            .build();
    }

    private void assertScanResponse(ScanResponseDTO response, String expectedItem, int expectedCurrentQuantity) {
        assertNotNull(response);
        assertEquals(Item.fromKey(expectedItem).getDisplayName(), response.getItemScanned());
        assertEquals(expectedCurrentQuantity, response.getCurrentQuantity());
        assertNotNull(response.getRunningTotal());
        assertNotNull(response.getMessage());
    }

    private void assertCartContains(String itemKey, int expectedQuantity) {
        CurrentItemsResponseDTO items = checkoutService.getCurrentItemsResponse();
        String displayName = Item.fromKey(itemKey).getDisplayName();

        boolean found = items.getItems().stream()
            .anyMatch(item -> displayName.equals(item.getItemName()) && item.getQuantity() == expectedQuantity);

        assertTrue(found, String.format("Expected %s with quantity %d in cart", displayName, expectedQuantity));
    }

    private void assertCartEmpty() {
        CurrentItemsResponseDTO items = checkoutService.getCurrentItemsResponse();
        assertTrue(items.getItems().isEmpty());
        assertEquals("$0.00", checkoutService.getCurrentTotal().getFinalTotal());
    }
}
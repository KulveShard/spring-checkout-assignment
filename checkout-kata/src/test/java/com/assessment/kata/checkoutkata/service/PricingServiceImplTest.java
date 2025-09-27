package com.assessment.kata.checkoutkata.service;

import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdateOfferRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePriceRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;
import com.assessment.kata.checkoutkata.exception.PricingValidationException;
import com.assessment.kata.checkoutkata.model.PricingConfig;
import com.assessment.kata.checkoutkata.repository.PricingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("PricingServiceImpl Business Logic Tests")
class PricingServiceImplTest {

  @Autowired private PricingService pricingService;
  @Autowired private PricingRepository pricingRepository;
  @Autowired private EntityManager entityManager;

  // Test data constants
  private static final String APPLE = "apple";
  private static final String BANANA = "banana";
  private static final String PEACH = "peach";
  private static final String KIWI = "kiwi";
  private static final String INVALID_ITEM = "invaliditem";

  @BeforeEach
  void setUp() {
    pricingRepository.deleteAll();
    createTestPricingData();
  }

  @Test
  @DisplayName("getAllPricingResponses should return all pricing configurations")
  void shouldReturnAllPricingConfigurations() {
    Map<String, FullPricingResponseDTO> allPricing = pricingService.getAllPricingResponses();
    assertEquals(4, allPricing.size());

    assertPricingDTO(APPLE, 30, "$0.30", 2, 15, "2 for $0.45 (save $0.15)");
    assertPricingDTO(BANANA, 50, "$0.50", 3, 20, "3 for $1.30 (save $0.20)");
    assertPricingDTO(PEACH, 60, "$0.60", null, null, null);
    assertPricingDTO(KIWI, 20, "$0.20", null, null, null);
  }

  @Test
  @DisplayName("updateItemPriceByName should successfully update item price")
  void shouldUpdateItemPriceSuccessfully() {
    UpdatePriceRequestDTO request = UpdatePriceRequestDTO.builder().newPriceInCents(40).build();
    UpdatePricingResponseDTO response = pricingService.updateItemPriceByName(APPLE, request);

    assertUpdateResponse(response, "Apple", "Price updated");
    assertEquals(30, response.getOldPriceInCents());
    assertEquals(40, response.getNewPriceInCents());
    assertDatabaseOffer(APPLE, 40, 2, 15); // Price updated, offer unchanged
  }

  @Test
  @DisplayName("updateItemPriceByName should throw IllegalArgumentException for invalid item")
  void shouldThrowIllegalArgumentExceptionForInvalidItem() {
    UpdatePriceRequestDTO request = UpdatePriceRequestDTO.builder().newPriceInCents(100).build();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> pricingService.updateItemPriceByName(INVALID_ITEM, request));
    assertTrue(exception.getMessage().contains("No item found with key"));
  }

  @Test
  @DisplayName("updateItemOfferByName should successfully add new offer to item without offer")
  void shouldAddNewOfferToItemWithoutOffer() {
    UpdateOfferRequestDTO request = UpdateOfferRequestDTO.builder().quantity(2).savingsInCents(20).build();
    UpdatePricingResponseDTO response = pricingService.updateItemOfferByName(PEACH, request);

    assertUpdateResponse(response, "Peach", "Added Peach offer");
    assertNull(response.getOldOfferDescription());
    assertEquals("2 for $1.00 (save $0.20)", response.getNewOfferDescription());
    assertDatabaseOffer(PEACH, 60, 2, 20); // Price unchanged, offer added
  }

  @Test
  @DisplayName("updateItemOfferByName should successfully update existing offer")
  void shouldUpdateExistingOffer() {
    UpdateOfferRequestDTO request = UpdateOfferRequestDTO.builder().quantity(3).savingsInCents(25).build();
    UpdatePricingResponseDTO response = pricingService.updateItemOfferByName(APPLE, request);

    assertUpdateResponse(response, "Apple", "Updated Apple offer");
    assertEquals("2 for $0.45 (save $0.15)", response.getOldOfferDescription());
    assertEquals("3 for $0.65 (save $0.25)", response.getNewOfferDescription());
    assertDatabaseOffer(APPLE, 30, 3, 25); // Price unchanged, offer updated
  }

  @Test
  @DisplayName("updateItemOfferByName should throw PricingValidationException when savings exceed total price")
  void shouldThrowPricingValidationExceptionWhenSavingsExceedTotalPrice() {
    UpdateOfferRequestDTO request = UpdateOfferRequestDTO.builder().quantity(2).savingsInCents(60).build();

    PricingValidationException exception = assertThrows(PricingValidationException.class,
        () -> pricingService.updateItemOfferByName(APPLE, request));
    assertEquals("Savings cannot exceed total price", exception.getMessage());
    assertDatabaseOffer(APPLE, 30, 2, 15); // Original offer unchanged
  }

  @Test
  @DisplayName("updateItemOfferByName should throw IllegalArgumentException for invalid item")
  void shouldThrowIllegalArgumentExceptionForInvalidItemInOffer() {
    UpdateOfferRequestDTO request = UpdateOfferRequestDTO.builder().quantity(2).savingsInCents(10).build();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> pricingService.updateItemOfferByName(INVALID_ITEM, request));
    assertTrue(exception.getMessage().contains("No item found with key"));
  }

  @Test
  @DisplayName("removeItemOfferByName should successfully remove existing offer")
  void shouldRemoveExistingOffer() {
    UpdatePricingResponseDTO response = pricingService.removeItemOfferByName(APPLE);

    assertUpdateResponse(response, "Apple", "Removed Apple offer");
    assertEquals("2 for $0.45 (save $0.15)", response.getOldOfferDescription());

    clearPersistenceContext(); // Required for @Modifying query
    assertDatabaseOffer(APPLE, 30, null, null); // Price unchanged, offer removed
  }

  @Test
  @DisplayName("removeItemOfferByName should handle removing offer when none exists")
  void shouldHandleRemovingOfferWhenNoneExists() {
    UpdatePricingResponseDTO response = pricingService.removeItemOfferByName(PEACH);

    assertUpdateResponse(response, "Peach", "Removed Peach offer");
    assertNull(response.getOldOfferDescription());
    assertDatabaseOffer(PEACH, 60, null, null); // No change
  }

  @Test
  @DisplayName("removeItemOfferByName should throw IllegalArgumentException for invalid item")
  void shouldThrowIllegalArgumentExceptionForInvalidItemInRemove() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> pricingService.removeItemOfferByName(INVALID_ITEM));
    assertTrue(exception.getMessage().contains("No item found with key"));
  }

  @Test
  @DisplayName("Business logic integration test - complete workflow")
  void shouldHandleCompleteWorkflow() {
    assertEquals(4, pricingService.getAllPricingResponses().size());

    // Update Apple price and offer
    pricingService.updateItemPriceByName(APPLE, UpdatePriceRequestDTO.builder().newPriceInCents(35).build());
    pricingService.updateItemOfferByName(APPLE, UpdateOfferRequestDTO.builder().quantity(3).savingsInCents(20).build());

    // Add offer to Kiwi
    pricingService.updateItemOfferByName(KIWI, UpdateOfferRequestDTO.builder().quantity(5).savingsInCents(10).build());

    // Remove Banana offer
    pricingService.removeItemOfferByName(BANANA);

    clearPersistenceContext(); // Required for @Modifying query

    // Verify final state
    assertPricingDTO(APPLE, 35, "$0.35", 3, 20, "3 for $0.85 (save $0.20)");
    assertPricingDTO(KIWI, 20, "$0.20", 5, 10, "5 for $0.90 (save $0.10)");
    assertPricingDTO(BANANA, 50, "$0.50", null, null, null);
    assertPricingDTO(PEACH, 60, "$0.60", null, null, null);
  }

  private void createTestPricingData() {
    savePricingConfig(APPLE, 30, 2, 15);    // Apple: 30¢, 2 for 45¢ (save 15¢)
    savePricingConfig(BANANA, 50, 3, 20);   // Banana: 50¢, 3 for 130¢ (save 20¢)
    savePricingConfig(PEACH, 60, null, null); // Peach: 60¢, no offer
    savePricingConfig(KIWI, 20, null, null);  // Kiwi: 20¢, no offer
  }

  private void savePricingConfig(String itemKey, int priceInCents, Integer offerQuantity, Integer savingsInCents) {
    PricingConfig config = new PricingConfig();
    config.setItemKey(itemKey);
    config.setPriceInCents(priceInCents);
    config.setOfferQuantity(offerQuantity);
    config.setOfferSavingsInCents(savingsInCents);
    pricingRepository.save(config);
  }

  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  private void assertPricingDTO(String itemKey, int expectedPrice, String expectedFormatted,
                                Integer expectedOfferQty, Integer expectedSavings, String expectedOfferDesc) {
    Map<String, FullPricingResponseDTO> allPricing = pricingService.getAllPricingResponses();
    FullPricingResponseDTO dto = allPricing.get(itemKey);
    assertNotNull(dto);
    assertEquals(itemKey, dto.getItemKey());
    assertEquals(expectedPrice, dto.getPriceInCents());
    assertEquals(expectedFormatted, dto.getPriceFormatted());
    assertEquals(expectedOfferQty, dto.getOfferQuantity());
    assertEquals(expectedSavings, dto.getOfferSavingsInCents());
    assertEquals(expectedOfferDesc, dto.getOfferDescription());
  }

  private void assertUpdateResponse(UpdatePricingResponseDTO response, String expectedItemName, String messageContains) {
    assertNotNull(response);
    assertEquals(expectedItemName, response.getItemName());
    assertTrue(response.getMessage().contains(messageContains));
  }

  private void assertDatabaseOffer(String itemKey, int expectedPrice, Integer expectedOfferQty, Integer expectedSavings) {
    PricingConfig config = pricingRepository.findByItemKey(itemKey).orElseThrow();
    assertEquals(expectedPrice, config.getPriceInCents());
    assertEquals(expectedOfferQty, config.getOfferQuantity());
    assertEquals(expectedSavings, config.getOfferSavingsInCents());
  }

}
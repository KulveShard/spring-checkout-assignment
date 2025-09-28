package com.assessment.kata.checkoutkata.controller;

import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdateOfferRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePriceRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;
import com.assessment.kata.checkoutkata.service.PricingService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/pricing")
public class AdminPricingController {

  private final PricingService pricingService;

  public AdminPricingController(PricingService pricingService) {
    this.pricingService = pricingService;
  }

  @GetMapping
  public ResponseEntity<Map<String, FullPricingResponseDTO>> getAllPricing() {
    Map<String, FullPricingResponseDTO> response = pricingService.getAllPricingResponses();
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{itemName}/price")
  public ResponseEntity<UpdatePricingResponseDTO> updateItemPrice(
      @PathVariable String itemName,
      @RequestBody @Valid UpdatePriceRequestDTO request) {

    UpdatePricingResponseDTO response = pricingService.updateItemPriceByName(itemName, request);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{itemName}/offer")
  public ResponseEntity<UpdatePricingResponseDTO> updateItemOffer(
      @PathVariable String itemName,
      @RequestBody @Valid UpdateOfferRequestDTO request) {

    UpdatePricingResponseDTO response = pricingService.updateItemOfferByName(itemName, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{itemName}/offer")
  public ResponseEntity<UpdatePricingResponseDTO> removeItemOffer(@PathVariable String itemName) {
    UpdatePricingResponseDTO response = pricingService.removeItemOfferByName(itemName);
    return ResponseEntity.ok(response);
  }
}
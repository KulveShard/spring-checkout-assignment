package com.assessment.kata.checkoutkata.service;

import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdateOfferRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePriceRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;
import com.assessment.kata.checkoutkata.model.DiscountRule;
import com.assessment.kata.checkoutkata.model.Item;

import java.util.Map;
import java.util.Optional;

public interface PricingService {

  UpdatePricingResponseDTO updateItemPriceByName(String itemName, UpdatePriceRequestDTO request);
  UpdatePricingResponseDTO updateItemOfferByName(String itemName, UpdateOfferRequestDTO request);
  UpdatePricingResponseDTO removeItemOfferByName(String itemName);
  Map<String, FullPricingResponseDTO> getAllPricingResponses();

  Optional<DiscountRule> getDiscountRule(Item item);
  int getItemPriceInCents(Item item);
  boolean hasItemConfiguration(Item item);
}
package com.assessment.kata.checkoutkata.service;

import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdateOfferRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePriceRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;

import java.util.Map;

public interface PricingService {

  UpdatePricingResponseDTO updateItemPriceByName(String itemName, UpdatePriceRequestDTO request);
  UpdatePricingResponseDTO updateItemOfferByName(String itemName, UpdateOfferRequestDTO request);
  UpdatePricingResponseDTO removeItemOfferByName(String itemName);
  Map<String, FullPricingResponseDTO> getAllPricingResponses();
}
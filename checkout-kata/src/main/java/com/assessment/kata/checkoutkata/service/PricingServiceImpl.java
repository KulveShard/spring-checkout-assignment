package com.assessment.kata.checkoutkata.service;

import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdateOfferRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePriceRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PricingServiceImpl implements PricingService{
  @Override
  public UpdatePricingResponseDTO updateItemPriceByName(String itemName, UpdatePriceRequestDTO request) {
    return null;
  }

  @Override
  public UpdatePricingResponseDTO updateItemOfferByName(String itemName, UpdateOfferRequestDTO request) {
    return null;
  }

  @Override
  public UpdatePricingResponseDTO removeItemOfferByName(String itemName) {
    return null;
  }

  @Override
  public Map<String, FullPricingResponseDTO> getAllPricingResponses() {
    return Map.of();
  }
}

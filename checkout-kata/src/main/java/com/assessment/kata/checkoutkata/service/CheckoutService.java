package com.assessment.kata.checkoutkata.service;

import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ItemQuantityRequestDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.exception.ItemNotFoundException;

public interface CheckoutService {
  ScanResponseDTO scanItems(ItemQuantityRequestDTO request) throws ItemNotFoundException;
  ScanResponseDTO removeItems(ItemQuantityRequestDTO request) throws ItemNotFoundException;
  CheckoutSummaryResponseDTO getCurrentTotal();
  CurrentItemsResponseDTO getCurrentItemsResponse();
  void clearCart();
}

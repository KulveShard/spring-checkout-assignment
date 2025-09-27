package com.assessment.kata.checkoutkata.service;

import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ItemQuantityRequestDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.exception.ItemNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CheckoutServiceImpl implements CheckoutService{
  @Override
  public ScanResponseDTO scanItems(ItemQuantityRequestDTO request) throws ItemNotFoundException {
    return null;
  }

  @Override
  public ScanResponseDTO removeItems(ItemQuantityRequestDTO request) throws ItemNotFoundException {
    return null;
  }

  @Override
  public CheckoutSummaryResponseDTO getCurrentTotal() {
    return null;
  }

  @Override
  public CurrentItemsResponseDTO getCurrentItemsResponse() {
    return null;
  }

  @Override
  public void clearCart() {

  }
}

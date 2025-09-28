package com.assessment.kata.checkoutkata.controller;

import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ItemQuantityRequestDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
  private final CheckoutService checkoutService;

  public CheckoutController(CheckoutService checkoutService) {
    this.checkoutService = checkoutService;
  }

  @PostMapping("/scan")
  public ResponseEntity<ScanResponseDTO> scanItems(@RequestBody @Valid ItemQuantityRequestDTO request) {
    ScanResponseDTO response = checkoutService.scanItems(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/remove")
  public ResponseEntity<ScanResponseDTO> removeItems(@RequestBody @Valid ItemQuantityRequestDTO request) {
    ScanResponseDTO response = checkoutService.removeItems(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/total")
  public ResponseEntity<CheckoutSummaryResponseDTO> getCurrentTotal() {
    CheckoutSummaryResponseDTO response = checkoutService.getCurrentTotal();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/items")
  public ResponseEntity<CurrentItemsResponseDTO> getCurrentItems() {
    CurrentItemsResponseDTO response = checkoutService.getCurrentItemsResponse();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/clear")
  public ResponseEntity<Void> clearCart() {
    checkoutService.clearCart();
    return ResponseEntity.ok().build();
  }
}

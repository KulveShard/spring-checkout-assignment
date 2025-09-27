package com.assessment.kata.checkoutkata.cli;

import com.assessment.kata.checkoutkata.dto.checkout.CheckoutSummaryResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.CurrentItemsResponseDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ItemQuantityRequestDTO;
import com.assessment.kata.checkoutkata.dto.checkout.ScanResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdateOfferRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePriceRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.assessment.kata.checkoutkata.cli.CheckoutConstants.*;

@Component
public class CheckoutApiClient {

    private final RestTemplate restTemplate;

    public CheckoutApiClient() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public Map<String, FullPricingResponseDTO> getAllPricing() {
        try {
            ResponseEntity<Map<String, FullPricingResponseDTO>> response = restTemplate.exchange(
                BASE_URL + ADMIN_PRICING_ENDPOINT,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );
            return response.getBody() != null ? response.getBody() : new HashMap<>();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to get pricing information: " + e.getMessage());
        }
    }

    public CheckoutSummaryResponseDTO getCurrentTotal() {
        try {
            return restTemplate.getForObject(BASE_URL + CHECKOUT_TOTAL_ENDPOINT, CheckoutSummaryResponseDTO.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to get current total: " + e.getMessage());
        }
    }

    public CurrentItemsResponseDTO getCurrentItems() {
        try {
            return restTemplate.getForObject(BASE_URL + CHECKOUT_ITEMS_ENDPOINT, CurrentItemsResponseDTO.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to get current items: " + e.getMessage());
        }
    }

    public ScanResponseDTO scanItems(String itemName, int quantity) {
        try {
            ItemQuantityRequestDTO request = new ItemQuantityRequestDTO();
            request.setItemName(itemName);
            request.setQuantity(quantity);
            return restTemplate.postForObject(BASE_URL + CHECKOUT_SCAN_ENDPOINT, request, ScanResponseDTO.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to scan items: " + e.getMessage());
        }
    }

    public void clearCart() {
        try {
            restTemplate.postForObject(BASE_URL + CHECKOUT_CLEAR_ENDPOINT, null, Void.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to clear cart: " + e.getMessage());
        }
    }

    public UpdatePricingResponseDTO updatePrice(String itemName, int newPriceInCents) {
        try {
            UpdatePriceRequestDTO request = new UpdatePriceRequestDTO();
            request.setNewPriceInCents(newPriceInCents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UpdatePriceRequestDTO> entity = new HttpEntity<>(request, headers);

            ResponseEntity<UpdatePricingResponseDTO> response = restTemplate.exchange(
                BASE_URL + String.format(PRICING_PRICE_ENDPOINT, itemName),
                HttpMethod.PATCH,
                entity,
                UpdatePricingResponseDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to update price: " + e.getMessage());
        }
    }

    public UpdatePricingResponseDTO updateOffer(String itemName, int quantity, int savingsInCents) {
        try {
            UpdateOfferRequestDTO request = new UpdateOfferRequestDTO();
            request.setQuantity(quantity);
            request.setSavingsInCents(savingsInCents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UpdateOfferRequestDTO> entity = new HttpEntity<>(request, headers);

            ResponseEntity<UpdatePricingResponseDTO> response = restTemplate.exchange(
                BASE_URL + String.format(PRICING_OFFER_ENDPOINT, itemName),
                HttpMethod.PATCH,
                entity,
                UpdatePricingResponseDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to update offer: " + e.getMessage());
        }
    }

    public void removeOffer(String itemName) {
        try {
            restTemplate.exchange(
                BASE_URL + String.format(PRICING_OFFER_ENDPOINT, itemName),
                HttpMethod.DELETE,
                null,
                UpdatePricingResponseDTO.class
            );
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to remove offer: " + e.getMessage());
        }
    }
}
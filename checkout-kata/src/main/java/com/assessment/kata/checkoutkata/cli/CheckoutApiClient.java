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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

import static com.assessment.kata.checkoutkata.cli.CheckoutConstants.*;

@Component
public class CheckoutApiClient {

    private final WebClient webClient;

    public CheckoutApiClient() {
        this.webClient = WebClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public Map<String, FullPricingResponseDTO> getAllPricing() {
        try {
            Map<String, FullPricingResponseDTO> response = webClient.get()
                .uri(ADMIN_PRICING_ENDPOINT)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, FullPricingResponseDTO>>() {})
                .block();
            return response != null ? response : new HashMap<>();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to get pricing information: " + e.getMessage());
        }
    }

    public CheckoutSummaryResponseDTO getCurrentTotal() {
        try {
            return webClient.get()
                .uri(CHECKOUT_TOTAL_ENDPOINT)
                .retrieve()
                .bodyToMono(CheckoutSummaryResponseDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to get current total: " + e.getMessage());
        }
    }

    public CurrentItemsResponseDTO getCurrentItems() {
        try {
            return webClient.get()
                .uri(CHECKOUT_ITEMS_ENDPOINT)
                .retrieve()
                .bodyToMono(CurrentItemsResponseDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to get current items: " + e.getMessage());
        }
    }

    public ScanResponseDTO scanItems(String itemName, int quantity) {
        try {
            ItemQuantityRequestDTO request = new ItemQuantityRequestDTO();
            request.setItemName(itemName);
            request.setQuantity(quantity);
            return webClient.post()
                .uri(CHECKOUT_SCAN_ENDPOINT)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ScanResponseDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to scan items: " + e.getMessage());
        }
    }

    public void clearCart() {
        try {
            webClient.post()
                .uri(CHECKOUT_CLEAR_ENDPOINT)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to clear cart: " + e.getMessage());
        }
    }

    public UpdatePricingResponseDTO updatePrice(String itemName, int newPriceInCents) {
        try {
            UpdatePriceRequestDTO request = new UpdatePriceRequestDTO();
            request.setNewPriceInCents(newPriceInCents);

            return webClient.patch()
                .uri(String.format(PRICING_PRICE_ENDPOINT, itemName))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UpdatePricingResponseDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to update price: " + e.getMessage());
        }
    }

    public UpdatePricingResponseDTO updateOffer(String itemName, int quantity, int savingsInCents) {
        try {
            UpdateOfferRequestDTO request = new UpdateOfferRequestDTO();
            request.setQuantity(quantity);
            request.setSavingsInCents(savingsInCents);

            return webClient.patch()
                .uri(String.format(PRICING_OFFER_ENDPOINT, itemName))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UpdatePricingResponseDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to update offer: " + e.getMessage());
        }
    }

    public void removeOffer(String itemName) {
        try {
            webClient.delete()
                .uri(String.format(PRICING_OFFER_ENDPOINT, itemName))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to remove offer: " + e.getMessage());
        }
    }
}
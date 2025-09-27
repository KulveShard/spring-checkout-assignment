package com.assessment.kata.checkoutkata.mapper;

import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;
import com.assessment.kata.checkoutkata.model.Item;
import com.assessment.kata.checkoutkata.model.PricingConfig;

import java.util.Map;
import java.util.stream.Collectors;

import static com.assessment.kata.checkoutkata.util.CheckoutStringUtils.formatOfferDescription;
import static com.assessment.kata.checkoutkata.util.CheckoutStringUtils.formatPrice;

public class AdminResponseMapper {

    public static UpdatePricingResponseDTO toPriceUpdateResponseDTO(Item item, int oldPriceInCents, int newPriceInCents, String updateSummary) {

        return UpdatePricingResponseDTO.builder()
            .message(updateSummary)
            .itemName(item.getDisplayName())
            .oldPriceInCents(oldPriceInCents)
            .newPriceInCents(newPriceInCents)
            .build();
    }

    public static UpdatePricingResponseDTO toOfferUpdateResponseDTO(Item item, int quantity, int itemPriceInCents,
                                                                    int savingsInCents, String oldOfferDescription) {
        String newOfferDescription = formatOfferDescription(quantity, itemPriceInCents, savingsInCents);

        String message = oldOfferDescription != null ?
            String.format("Updated %s offer: %s → %s", item.getDisplayName(), oldOfferDescription, newOfferDescription) :
            String.format("Added %s offer: %s", item.getDisplayName(), newOfferDescription);

        return UpdatePricingResponseDTO.builder()
            .message(message)
            .itemName(item.getDisplayName())
            .oldOfferDescription(oldOfferDescription)
            .newOfferDescription(newOfferDescription)
            .build();
    }

    public static UpdatePricingResponseDTO toOfferRemovalResponseDTO(Item item, String oldOfferDescription) {
        String message = String.format("Removed %s offer: %s", item.getDisplayName(),
            oldOfferDescription != null ? oldOfferDescription : "no offer found");

        return UpdatePricingResponseDTO.builder()
            .message(message)
            .itemName(item.getDisplayName())
            .oldOfferDescription(oldOfferDescription)
            .build();
    }

    public static FullPricingResponseDTO toFullPricingResponseDTO(String itemKey, PricingConfig config) {
        if (config == null) {
            return null;
        }

        String offerDescription = null;
        Integer offerQuantity = null;
        Integer savingsInCents = null;

        if (config.hasOffer()) {
            offerQuantity = config.getOfferQuantity();
            savingsInCents = config.getOfferSavingsInCents();
            int effectivePriceForSet = config.getPriceInCents() * offerQuantity - savingsInCents;
            offerDescription = String.format("%d for %s (save %s)",
                offerQuantity,
                formatPrice(effectivePriceForSet),
                formatPrice(savingsInCents));
        }

        return new FullPricingResponseDTO(
            itemKey,
            config.getPriceInCents(),
            formatPrice(config.getPriceInCents()),
            offerQuantity,
            savingsInCents,
            offerDescription
        );
    }

    public static Map<String, FullPricingResponseDTO> toPricingResponseMap(Map<String, PricingConfig> allPricing) {
        return allPricing.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> toFullPricingResponseDTO(e.getKey(), e.getValue())
            ));
    }
}
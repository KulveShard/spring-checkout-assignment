package com.assessment.kata.checkoutkata.service;

import com.assessment.kata.checkoutkata.dto.pricing.FullPricingResponseDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdateOfferRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePriceRequestDTO;
import com.assessment.kata.checkoutkata.dto.pricing.UpdatePricingResponseDTO;
import com.assessment.kata.checkoutkata.event.PriceUpdateEvent;
import com.assessment.kata.checkoutkata.event.UpdateType;
import com.assessment.kata.checkoutkata.exception.ItemNotFoundException;
import com.assessment.kata.checkoutkata.exception.PricingValidationException;
import com.assessment.kata.checkoutkata.model.DiscountRule;
import com.assessment.kata.checkoutkata.model.Item;
import com.assessment.kata.checkoutkata.model.PricingConfig;
import com.assessment.kata.checkoutkata.repository.PricingRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.assessment.kata.checkoutkata.mapper.AdminRequestMapper.toItem;
import static com.assessment.kata.checkoutkata.mapper.AdminRequestMapper.toPriceInCents;
import static com.assessment.kata.checkoutkata.mapper.AdminResponseMapper.toOfferRemovalResponseDTO;
import static com.assessment.kata.checkoutkata.mapper.AdminResponseMapper.toOfferUpdateResponseDTO;
import static com.assessment.kata.checkoutkata.mapper.AdminResponseMapper.toPriceUpdateResponseDTO;
import static com.assessment.kata.checkoutkata.mapper.AdminResponseMapper.toPricingResponseMap;
import static com.assessment.kata.checkoutkata.util.CheckoutStringUtils.formatOldOfferDescription;
import static com.assessment.kata.checkoutkata.util.CheckoutStringUtils.formatPrice;

@Service
public class PricingServiceImpl implements PricingService {

  private final PricingRepository pricingRepository;
  private final ApplicationEventPublisher eventPublisher;

  public PricingServiceImpl(PricingRepository pricingRepository, ApplicationEventPublisher eventPublisher) {
    this.pricingRepository = pricingRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public UpdatePricingResponseDTO updateItemPriceByName(String itemName, UpdatePriceRequestDTO request) {
    Item item = toItem(itemName);
    int newPrice = toPriceInCents(request);

    PricingConfig pricingConfig = pricingRepository.findByItemKey(item.getKey())
        .orElseThrow(() -> new ItemNotFoundException("Item not found: " + item));

    int oldPrice = pricingConfig.getPriceInCents();
    pricingConfig.setPriceInCents(newPrice);
    pricingRepository.save(pricingConfig);

    String updateSummary = String.format("Price updated: %s %s → %s",
        item.getDisplayName(),
        formatPrice(oldPrice),
        formatPrice(newPrice));

    eventPublisher.publishEvent(PriceUpdateEvent.builder()
        .source(this)
        .item(item)
        .updateType(UpdateType.PRICE_CHANGED)
        .oldPriceInCents(oldPrice)
        .newPriceInCents(newPrice)
        .description(updateSummary));

    return toPriceUpdateResponseDTO(item, oldPrice, newPrice, updateSummary);
  }

  @Override
  public UpdatePricingResponseDTO updateItemOfferByName(String itemName, UpdateOfferRequestDTO request) {
    Item item = toItem(itemName);

    PricingConfig pricingConfig = pricingRepository.findByItemKey(item.getKey())
        .orElseThrow(() -> new ItemNotFoundException("Item not found: " + item));

    int itemPrice = pricingConfig.getPriceInCents();
    int totalWithoutDiscount = itemPrice * request.getQuantity();

    if (request.getSavingsInCents() >= totalWithoutDiscount) {
      throw new PricingValidationException("Savings cannot exceed total price");
    }

    Optional<DiscountRule> oldRule = pricingConfig.hasOffer() ?
        Optional.of(new DiscountRule(pricingConfig.getOfferQuantity(), pricingConfig.getOfferSavingsInCents())) :
        Optional.empty();

    pricingConfig.setOfferQuantity(request.getQuantity());
    pricingConfig.setOfferSavingsInCents(request.getSavingsInCents());
    pricingRepository.save(pricingConfig);

    String description = createOfferUpdateDescription(request, oldRule, item, itemPrice);

    eventPublisher.publishEvent(PriceUpdateEvent.builder()
        .source(this)
        .item(item)
        .updateType(UpdateType.OFFER_CHANGED)
        .description(description)
        .build());

    String oldOfferDescription = oldRule.map(rule ->
        formatOldOfferDescription(rule.getRequiredQuantity(), itemPrice, rule.getSavingsInCents())
    ).orElse(null);

    return toOfferUpdateResponseDTO(item, request.getQuantity(), itemPrice,
        request.getSavingsInCents(), oldOfferDescription);
  }

  @Override
  @Transactional
  public UpdatePricingResponseDTO removeItemOfferByName(String itemName) {
    Item item = toItem(itemName);

    PricingConfig pricingConfig = pricingRepository.findByItemKey(item.getKey())
        .orElseThrow(() -> new ItemNotFoundException("Item not found: " + item));

    // Get old rule before removing
    Optional<DiscountRule> oldRule = pricingConfig.hasOffer() ?
        Optional.of(new DiscountRule(pricingConfig.getOfferQuantity(), pricingConfig.getOfferSavingsInCents())) :
        Optional.empty();

    if (oldRule.isPresent()) {
      pricingRepository.removeOfferByItemKey(item.getKey());
      pricingRepository.save(pricingConfig);

      String description = String.format("Offer removed: %s (was %d for %s)",
          item.getDisplayName(),
          oldRule.get().getRequiredQuantity(),
          formatPrice(getItemPriceInCents(item) * oldRule.get().getRequiredQuantity() - oldRule.get().getSavingsInCents()));

      eventPublisher.publishEvent(PriceUpdateEvent.builder()
          .source(this)
          .item(item)
          .updateType(UpdateType.OFFER_REMOVED)
          .description(description)
          .build());
    }

    String oldOfferDescription = oldRule.map(rule ->
        formatOldOfferDescription(rule.getRequiredQuantity(),
            pricingConfig.getPriceInCents(),
            rule.getSavingsInCents())
    ).orElse(null);

    return toOfferRemovalResponseDTO(item, oldOfferDescription);
  }

  @Override
  public Map<String, FullPricingResponseDTO> getAllPricingResponses() {
    return toPricingResponseMap(getAllPricing());
  }

  @Cacheable(value = "pricing", key = "'all'")
  public Map<String, PricingConfig> getAllPricing() {
    Map<String, PricingConfig> result = new HashMap<>();
    pricingRepository.findAll().forEach(entity -> result.put(entity.getItemKey(), entity));
    return result;
  }

  @Cacheable(value = "pricing", key = "#item.key + '_price'")
  public int getItemPriceInCents(Item item) {
    Optional<PricingConfig> config = pricingRepository.findByItemKey(item.getKey());
    if (config.isEmpty()) {
      throw new ItemNotFoundException(String.format("No pricing configuration for item %s", item));
    }
    return config.get().getPriceInCents();
  }

  private String createOfferUpdateDescription(UpdateOfferRequestDTO request, Optional<DiscountRule> oldRule, Item item, int itemPrice) {
    String description;
    if (oldRule.isPresent()) {
      description = String.format("Offer updated: %s %d for %s → %d for %s (save %s)",
          item.getDisplayName(),
          oldRule.get().getRequiredQuantity(),
          formatPrice(itemPrice * oldRule.get().getRequiredQuantity() - oldRule.get().getSavingsInCents()),
          request.getQuantity(),
          formatPrice(itemPrice * request.getQuantity() - request.getSavingsInCents()),
          formatPrice(request.getSavingsInCents()));
    } else {
      description = String.format("New offer: %s %d for %s (save %s)",
          item.getDisplayName(),
          request.getQuantity(),
          formatPrice(itemPrice * request.getQuantity() - request.getSavingsInCents()),
          formatPrice(request.getSavingsInCents()));
    }
    return description;
  }
}

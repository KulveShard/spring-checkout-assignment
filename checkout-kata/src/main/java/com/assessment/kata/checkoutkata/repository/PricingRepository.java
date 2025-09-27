package com.assessment.kata.checkoutkata.repository;

import com.assessment.kata.checkoutkata.model.PricingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRepository extends JpaRepository<PricingConfig, String> {

  @Query("SELECT p FROM PricingConfig p WHERE p.offerQuantity IS NOT NULL AND p.offerSavingsInCents IS NOT NULL")
  List<PricingConfig> findAllWithOffers();

  @Modifying
  @Query("UPDATE PricingConfig p SET p.offerQuantity = null, p.offerSavingsInCents = null WHERE p.itemKey = :itemKey")
  void removeOfferByItemKey(String itemKey);

  Optional<PricingConfig> findByItemKey(String itemKey);

  boolean existsByItemKey(String itemKey);
}
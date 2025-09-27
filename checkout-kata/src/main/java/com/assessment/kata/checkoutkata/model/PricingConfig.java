package com.assessment.kata.checkoutkata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "pricing_config")
public class PricingConfig {

  @Id
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "item_key", nullable = false, length = 20)
  private String itemKey;

  @NotNull
  @Min(1)
  @Column(name = "price_in_cents", nullable = false)
  private Integer priceInCents;

  @Min(2)
  @Column(name = "offer_quantity")
  private Integer offerQuantity;

  @Min(1)
  @Column(name = "offer_savings_in_cents")
  private Integer offerSavingsInCents;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public boolean hasOffer() {
    return offerQuantity != null && offerSavingsInCents != null;
  }

}
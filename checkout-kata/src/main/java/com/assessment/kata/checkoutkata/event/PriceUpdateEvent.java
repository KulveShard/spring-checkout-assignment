package com.assessment.kata.checkoutkata.event;

import com.assessment.kata.checkoutkata.model.Item;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
@EqualsAndHashCode
public class PriceUpdateEvent extends ApplicationEvent {

  private final Item item;
  private final UpdateType updateType;
  private final Integer oldPriceInCents;
  private final Integer newPriceInCents;
  private final String description;

  @Builder
  public PriceUpdateEvent(Object source, Item item, UpdateType updateType,
                          Integer oldPriceInCents, Integer newPriceInCents, String description) {
    super(source);
    this.item = item;
    this.updateType = updateType;
    this.oldPriceInCents = oldPriceInCents;
    this.newPriceInCents = newPriceInCents;
    this.description = description;
  }
}
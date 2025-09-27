package com.assessment.kata.checkoutkata.mapper;

import com.assessment.kata.checkoutkata.dto.pricing.UpdatePriceRequestDTO;
import com.assessment.kata.checkoutkata.model.Item;
import org.springframework.stereotype.Component;

public class AdminRequestMapper {

    public static Item toItem(String itemName) {
        return Item.fromKey(itemName);
    }

    public static int toPriceInCents(UpdatePriceRequestDTO request) {
        return request.getNewPriceInCents();
    }
}
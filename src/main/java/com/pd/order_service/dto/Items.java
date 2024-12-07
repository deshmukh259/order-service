package com.pd.order_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Items {
    private String itemName;
    private Long quantity;

    public Items(String itemName, Long quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }
}

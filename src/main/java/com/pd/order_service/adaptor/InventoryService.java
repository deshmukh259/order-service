package com.pd.order_service.adaptor;


import com.pd.order_service.dto.ItemDto;

public interface InventoryService {

    void getItems();

    void getItem();

    void getAvailableItems();

    void getAvailableItem();

    ItemDto getAvailableItems(String itemName);
}

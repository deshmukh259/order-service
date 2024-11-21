package com.pd.order_service.adaptor;


import com.pd.order_service.dto.ItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceAdaptor implements InventoryService {


    private final RestTemplate restTemplate;
    @Value("${inventory.url}")
    private String inventoryUrl;

    public InventoryServiceAdaptor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void getItems() {

    }

    public void getItem() {

    }

    public void getAvailableItems() {

    }

    public void getAvailableItem() {

    }

    @Override
    public ItemDto getAvailableItems(String itemName) {

        String url = inventoryUrl + "/api/available-items/" + itemName;
        ResponseEntity<ItemDto> forEntity = restTemplate.getForEntity(url, ItemDto.class);
        return forEntity.getBody();
    }

}

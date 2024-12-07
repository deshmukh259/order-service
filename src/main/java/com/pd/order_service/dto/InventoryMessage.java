package com.pd.order_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InventoryMessage {

    private List<Items> items = new ArrayList<>();

}


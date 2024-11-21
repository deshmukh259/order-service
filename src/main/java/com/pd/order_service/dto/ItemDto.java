package com.pd.order_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ItemDto {
    private long itemId;
    private String itemName;
    private int totalQty;
    private int soldQty;
    private String supplierDesc;
    private int price;
}

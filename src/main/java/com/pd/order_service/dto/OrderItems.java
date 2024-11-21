package com.pd.order_service.dto;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItems {
    private String itemName;
    private Long quantity;
}
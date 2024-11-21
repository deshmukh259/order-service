package com.pd.order_service.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private List<OrderItems> items;
    private String userName;
    private String address;


}

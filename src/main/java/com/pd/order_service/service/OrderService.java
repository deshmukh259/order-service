package com.pd.order_service.service;

import com.pd.order_service.dto.OrderDetailsDto;
import com.pd.order_service.dto.OrderDto;

import java.util.List;

public interface OrderService {

    List<OrderDetailsDto> getOrders();

    OrderDetailsDto getOrder(int orderId);

    List<OrderDetailsDto> createOrder(OrderDto orderDto);
}

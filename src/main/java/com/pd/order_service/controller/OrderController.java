package com.pd.order_service.controller;


import com.pd.order_service.dto.OrderDetailsDto;
import com.pd.order_service.dto.OrderDto;
import com.pd.order_service.excep.OrderException;
import com.pd.order_service.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/api/orders")
    public List<OrderDetailsDto> getOrderDetails() {

        return orderService.getOrders();

    }

    @GetMapping(value = "/api/orders/{orderId}")
    public OrderDetailsDto getOrderDetail(@PathVariable int orderId) throws OrderException {

        if (orderId < 1)
            throw new OrderException("Invalid OrderId!!");

        return orderService.getOrder(orderId);

    }

    @PostMapping(value = "/api/orders")
    public List<OrderDetailsDto> createOrder(@RequestBody OrderDto orderDto) {

        return orderService.createOrder(orderDto);

    }
}

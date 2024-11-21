package com.pd.order_service.service;

import com.pd.order_service.adaptor.InventoryService;
import com.pd.order_service.dto.ItemDto;
import com.pd.order_service.dto.OrderDetailsDto;
import com.pd.order_service.dto.OrderDto;
import com.pd.order_service.entity.OrderEntity;
import com.pd.order_service.entity.OrderItemEntity;
import com.pd.order_service.entity.OrderStatusEnum;
import com.pd.order_service.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;

    private final InventoryService inventoryService;


    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, InventoryService inventoryService, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<OrderDetailsDto> getOrders() {
        return convertToDto(orderRepository.findAll());
    }

    private List<OrderDetailsDto> convertToDto(List<OrderEntity> orderEntities) {
        return orderEntities.stream().map(e -> modelMapper.map(e, OrderDetailsDto.class)).collect(Collectors.toList());
    }

    @Override
    public OrderDetailsDto getOrder(int orderId) {

        return modelMapper.map(orderRepository.findById((long) orderId), OrderDetailsDto.class);
    }

    @Override
    public List<OrderDetailsDto> createOrder(OrderDto orderDto) {

        List<ItemDto> itemDtoList = new ArrayList<>();
        //check available quantity
        OrderEntity order = new OrderEntity.OrderEntityBuilder()
                .address(orderDto.getAddress())
                .userName(orderDto.getUserName())
                .status(OrderStatusEnum.ORDERED)
                .cancellable(true)
                .build();

        List<OrderItemEntity> orderItemEntities = orderDto.getItems().stream().map(item -> {
            ItemDto itemDto = inventoryService.getAvailableItems(item.getItemName());
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            if ((itemDto.getTotalQty() - itemDto.getSoldQty()) >= item.getQuantity()) {
                orderItemEntity.setOrderEntity(order);
                orderItemEntity.setItemName(item.getItemName());
                orderItemEntity.setQuantity(item.getQuantity());
                orderItemEntity.setStatus(OrderStatusEnum.ORDERED);
                orderItemEntity.setFinalCost((double) itemDto.getPrice());
            } else {
                throw new RuntimeException("Item Quantity not to sold : " + itemDto.getItemName());
            }
            return orderItemEntity;
        }).toList();
        order.setOrderItemEntityList(orderItemEntities);
        order = orderRepository.save(order);
        System.out.println("order created ");

        kafkaAdaptor.sendEvent();

        return List.of();
    }
}

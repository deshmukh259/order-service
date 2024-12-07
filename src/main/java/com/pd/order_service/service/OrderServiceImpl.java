package com.pd.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pd.order_service.adaptor.InventoryService;
import com.pd.order_service.adaptor.KafkaAdaptor;
import com.pd.order_service.dto.*;
import com.pd.order_service.entity.OrderEntity;
import com.pd.order_service.entity.OrderItemEntity;
import com.pd.order_service.entity.OrderStatusEnum;
import com.pd.order_service.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;

    private final InventoryService inventoryService;

    private final ObjectMapper objectMapper;


    private final ModelMapper modelMapper;

    private final String inventoryTopic;

    private final String shipmentTopic;

    private final KafkaAdaptor kafkaAdaptor;

    public OrderServiceImpl(OrderRepository orderRepository, InventoryService inventoryService, ObjectMapper objectMapper,
                            ModelMapper modelMapper, @Value("${kafka.topic.inventory}") String inventoryTopic,
                            @Value("${kafka.topic.shipment}") String shipmentTopic, KafkaAdaptor kafkaAdaptor) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.inventoryTopic = inventoryTopic;
        this.shipmentTopic = shipmentTopic;
        this.kafkaAdaptor = kafkaAdaptor;
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

        //check available quantity

        OrderEntity order = new OrderEntity();
        order.setAddress(orderDto.getAddress());
        order.setUserName(orderDto.getUserName());
        order.setStatus(OrderStatusEnum.ORDERED);
        order.setCancellable(true);

        OrderEntity finalOrder = order;
        List<OrderItemEntity> orderItemEntities = orderDto.getItems().stream().map(item -> {
            ItemDto itemDto = inventoryService.getAvailableItems(item.getItemName());
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            if ((itemDto.getTotalQty() - itemDto.getSoldQty()) >= item.getQuantity()) {
                orderItemEntity.setOrderEntity(finalOrder);
                orderItemEntity.setItemName(item.getItemName());
                orderItemEntity.setQuantity(item.getQuantity());
                orderItemEntity.setStatus(OrderStatusEnum.ORDERED);
                orderItemEntity.setFinalCost((double) itemDto.getPrice());
                Double totalCost = finalOrder.getTotalCost();
                if (totalCost == null || totalCost == 0) {
                    totalCost = (double) itemDto.getPrice() * item.getQuantity();
                } else {
                    totalCost += (double) itemDto.getPrice() * item.getQuantity();
                }
                finalOrder.setTotalCost(totalCost);
            } else {
                throw new RuntimeException("Item Quantity not to sold : " + itemDto.getItemName());
            }
            return orderItemEntity;
        }).toList();
        order.setOrderItemEntityList(orderItemEntities);
        order = orderRepository.save(order);
        System.out.println("order created ");

        String orderEvent = null;
        String shipmentEvent = null;
        try {
            orderEvent = createInventoryEvent(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            shipmentEvent = createShipmentEvent(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //shipment topic need to add address user details, and item details
        }
        kafkaAdaptor.sendMessage(inventoryTopic, orderEvent);
        kafkaAdaptor.sendMessage(shipmentTopic, shipmentEvent);

        return List.of();
    }

    private String createShipmentEvent(OrderEntity order) throws JsonProcessingException {

        InventoryMessage inventoryMessage = new InventoryMessage();
        for (OrderItemEntity orderItemEntity : order.getOrderItemEntityList()) {
            inventoryMessage.getItems().add(new Items(orderItemEntity.getItemName(), orderItemEntity.getQuantity()));
        }
        return objectMapper.writeValueAsString(inventoryMessage);

    }

    private String createInventoryEvent(OrderEntity order) throws JsonProcessingException {


        InventoryMessage inventoryMessage = new InventoryMessage();
        for (OrderItemEntity orderItemEntity : order.getOrderItemEntityList()) {
            inventoryMessage.getItems().add(new Items(orderItemEntity.getItemName(), orderItemEntity.getQuantity()));
        }
        return objectMapper.writeValueAsString(inventoryMessage);

//        inventory -> minus available qnty ->itemname -> sold qnty, [list]
//
//        shipment item ids orderid->qnty -> itemname -> sold qnty, [list]
//        address
    }
}


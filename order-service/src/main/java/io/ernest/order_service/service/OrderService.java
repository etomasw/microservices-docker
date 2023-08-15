package io.ernest.order_service.service;

import io.ernest.order_service.model.dto.BaseResponse;
import io.ernest.order_service.model.dto.OrderItemResponse;
import io.ernest.order_service.model.dto.OrderRequest;
import io.ernest.order_service.model.dto.OrderResponse;
import io.ernest.order_service.model.entity.Order;
import io.ernest.order_service.model.entity.OrderItem;
import io.ernest.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public OrderResponse create(OrderRequest request) {
        // Check for inventory
        BaseResponse response = this.webClientBuilder.build()
                .post()
                .uri("lb://inventory-service/api/inventory/in-stock")
                .bodyValue(request.getItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();

        if(response != null && response.hasErrors()) {
            throw new IllegalArgumentException("Items out of stock");
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setItems(request.getItems().stream()
                .map(item -> new OrderItem(item, order))
                .collect(Collectors.toList()));

        orderRepository.save(order);
        log.info("Order created with ID: {}", order.getOrderNumber());

        return mapToOrderResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> orderItems = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        return new OrderResponse(order.getId(), order.getOrderNumber(), orderItems);
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getSku(), item.getPrice(), item.getQuantity());
    }
}

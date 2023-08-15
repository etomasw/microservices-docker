package io.ernest.order_service.model.dto;

import io.ernest.order_service.model.entity.Order;

import java.util.List;

public record OrderResponse(Long id, String orderNumber, List<OrderItemResponse> items) {
}
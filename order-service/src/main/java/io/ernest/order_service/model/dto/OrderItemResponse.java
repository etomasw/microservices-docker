package io.ernest.order_service.model.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, String sku, double price, Long quantity) {
}

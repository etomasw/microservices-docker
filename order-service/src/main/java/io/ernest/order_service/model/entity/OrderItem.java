package io.ernest.order_service.model.entity;

import io.ernest.order_service.model.dto.OrderItemRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private Double price;
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem(OrderItemRequest request, Order order) {
        this.sku = request.getSku();
        this.price = request.getPrice();
        this.quantity = request.getQuantity();
        this.order = order;
    }
}

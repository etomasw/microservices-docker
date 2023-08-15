package io.ernest.inventory_service.services;

import io.ernest.inventory_service.model.dto.BaseResponse;
import io.ernest.inventory_service.model.dto.OrderItemRequest;
import io.ernest.inventory_service.model.entities.Inventory;
import io.ernest.inventory_service.repositories.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }


    public boolean isInStock(String sku) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findBySku(sku);
        if(inventoryOpt.isPresent()) {
            return inventoryOpt.get().getQuantity() > 0;
        }
        return false;
    }

    public BaseResponse areInStock(List<OrderItemRequest> orderItems) {
        List<String> errorList = new ArrayList<>();
        List<String> skus = orderItems.stream()
                .map(OrderItemRequest::getSku)
                .collect(Collectors.toList());

        Map<String, Inventory> inventoriesBySku = inventoryRepository.findBySkuIn(skus).stream()
                .collect(Collectors.toMap(Inventory::getSku, c -> c));
        orderItems.forEach(item -> {
            Inventory inventory = inventoriesBySku.getOrDefault(item.getSku(), null);
            if(inventory != null) {
                errorList.add("Product with sku " + item.getSku() + " does not exist");
            } else if(item.getQuantity() > inventory.getQuantity()) {
                errorList.add("Product with sku " + item.getSku() + " does not have enough stock");
            }
        });

        return errorList.isEmpty() ? new BaseResponse(errorList.toArray(new String[0])) : new BaseResponse(null);
    }
}

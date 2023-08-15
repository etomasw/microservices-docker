package io.ernest.products_service.service;

import io.ernest.products_service.model.dto.ProductRequest;
import io.ernest.products_service.model.dto.ProductResponse;
import io.ernest.products_service.model.entities.Product;
import io.ernest.products_service.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProduct(ProductRequest request) {
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(request.getStatus())
                .build();

        Product productDb = productRepository.save(product);
        log.info("Product saved to DB with ID: {}", productDb.getId());
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }
}

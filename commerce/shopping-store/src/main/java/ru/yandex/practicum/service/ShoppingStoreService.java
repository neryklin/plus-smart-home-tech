package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {
    boolean setQuantity(UUID productId, QuantityState state);

    boolean removedProductById(UUID productId);

    ProductDto getProductById(UUID productId);

    Page<ProductDto> get(ProductCategory category, Pageable pageable);

    ProductDto create(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    List<ProductDto> getProductsByIds(List<UUID> uuids);
}

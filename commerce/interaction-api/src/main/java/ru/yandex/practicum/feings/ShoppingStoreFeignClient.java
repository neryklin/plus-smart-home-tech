package ru.yandex.practicum.feings;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreFeignClient {
    @GetMapping
    Page<ProductDto> get(@RequestParam ProductCategory category, Pageable pageable);

    @GetMapping("{productId}")
    ProductDto getProductById(@PathVariable UUID productId);

    @PutMapping
    ProductDto create(@RequestBody ProductDto productDto);

    @PostMapping
    ProductDto update(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    boolean remove(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setQuantity(SetQuantityStateRequest request);

    @GetMapping("/by-ids")
    List<ProductDto> getProductsByIds(@RequestBody List<UUID> uuids);
}
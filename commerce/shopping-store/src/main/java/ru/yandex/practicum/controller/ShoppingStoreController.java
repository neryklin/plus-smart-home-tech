package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.feings.ShoppingStoreFeignClient;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreFeignClient {
    private final ShoppingStoreService shoppingStoreService;


    @PutMapping
    public ProductDto create(@RequestBody ProductDto productDto) {
        log.info("[PUT] новый товара: {}", productDto);
        ProductDto pr = shoppingStoreService.create(productDto);
        log.info("[PUT] новый товара: {}", pr);
        return pr;
    }

    @PostMapping("/removeProductFromStore")
    public boolean remove(@RequestBody UUID productId) {
        log.info("[POST] Удаление товара c id: {}", productId);
        return shoppingStoreService.removedProductById(productId);
    }

    @PostMapping
    public ProductDto update(@RequestBody ProductDto productDto) {
        log.info("[POST] апдейт товар в ассортименте: {}", productDto);
        return shoppingStoreService.update(productDto);
    }


    @PostMapping("/quantityState")
    public boolean setQuantity(SetQuantityStateRequest request) {
        log.info("[POST] Статус по товару c id: {}, статус: {}", request.getProductId(), request.getQuantityState());
        return shoppingStoreService.setQuantity(request.getProductId(), request.getQuantityState());
    }

    @GetMapping
    public Page<ProductDto> get(@RequestParam ProductCategory category, Pageable pageable) {
        log.info("[GET] товары с категорий: {}, странично: {}", category, pageable);
        return shoppingStoreService.get(category, pageable);
    }

    @GetMapping("{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        log.info("[GET] товар c id: {}", productId);
        return shoppingStoreService.getProductById(productId);
    }
}

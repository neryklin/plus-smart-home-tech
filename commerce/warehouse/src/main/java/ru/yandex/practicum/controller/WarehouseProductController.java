package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.feings.WarehouseProductFeignClient;
import ru.yandex.practicum.service.WarehouseProductService;


@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseProductController implements WarehouseProductFeignClient {
    private final WarehouseProductService warehouseProductService;

    @PostMapping("/add")
    public void addQuantity(@RequestBody AddProductToWarehouseRequest request) {
        log.info("[POST] проверка товара на количество: {}", request);
        warehouseProductService.addQuantity(request);
    }

    @PutMapping
    public void registerNewProduct(@Valid @RequestBody NewProductInWarehouseRequest request) {
        log.info("[PUT] Запрос нового товара для склада: {},", request);
        warehouseProductService.newProduct(request);
    }

    @PostMapping("/check")
    public BookedProductsDto checkAvailableProducts(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("[POST] Проверка корзины на доступность: {}", shoppingCartDto);
        return warehouseProductService.checkAvailableProducts(shoppingCartDto);
    }


    @GetMapping("address")
    public AddressDto getAddressWarehouse() {
        log.info("[GET] запрос адреса склада");
        return warehouseProductService.getAddressWarehouse();
    }
}
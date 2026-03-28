package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.feings.WarehouseProductFeignClient;
import ru.yandex.practicum.model.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.model.ShipperToDeliveryRequest;
import ru.yandex.practicum.service.WarehouseProductService;

import java.util.Map;
import java.util.UUID;


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
    public BookedProductsDto reserveProducts(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("[POST] Проверка корзины на доступность: {}", shoppingCartDto);
        return warehouseProductService.checkAvailableProducts(shoppingCartDto);
    }


    @GetMapping("address")
    public AddressDto getAddressWarehouse() {
        log.info("[GET] запрос адреса склада");
        return warehouseProductService.getAddressWarehouse();
    }

    @Override
    public void shippedToDelivery(ru.yandex.practicum.dto.ShipperToDeliveryRequest request) {

    }

    @PostMapping("/shipped")
    public void shippedToDelivery(@Valid @RequestBody ShipperToDeliveryRequest request) {
        log.info("[POST] Запрос на передачу товара в доставку: {}", request);
        warehouseProductService.shippedToDelivery(request);
    }

    @PostMapping("/return")
    public void returnProducts(@RequestBody Map<UUID, Long> products) {
        log.info("[POST] Возврат товаров на склад: {}", products);
        warehouseProductService.returnProducts(products);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(ru.yandex.practicum.dto.AssemblyProductsForOrderRequest request) {
        return null;
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(@Valid @RequestBody AssemblyProductsForOrderRequest request) {
        log.info("[POST] Запрос на сборку товаров: {}", request);
        return warehouseProductService.assemblyProductsForOrder(request);
    }
}
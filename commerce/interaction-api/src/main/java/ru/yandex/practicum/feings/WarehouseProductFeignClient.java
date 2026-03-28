package ru.yandex.practicum.feings;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.*;

import java.util.Map;
import java.util.UUID;


@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallbackFactory = WarehouseFeignClientFallback.class)
public interface WarehouseProductFeignClient {
    @PutMapping
    void registerNewProduct(@Valid @RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto reserveProducts(@Valid @RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addQuantity(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("address")
    AddressDto getAddressWarehouse();

    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody @Valid ShipperToDeliveryRequest request);

    @PostMapping("/return")
    void returnProducts(@RequestBody Map<UUID, Long> products);

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request);
}
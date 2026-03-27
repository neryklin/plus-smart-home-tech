package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.model.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.model.ShipperToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseProductService {
    void newProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkAvailableProducts(ShoppingCartDto shoppingCartDto);

    void addQuantity(AddProductToWarehouseRequest request);

    AddressDto getAddressWarehouse();

    void shippedToDelivery(ShipperToDeliveryRequest request);

    void returnProducts(Map<UUID, Long> products);

    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);
}
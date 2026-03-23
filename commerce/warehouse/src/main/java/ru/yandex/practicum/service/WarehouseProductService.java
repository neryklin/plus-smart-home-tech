package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.*;

public interface WarehouseProductService {
    void newProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkAvailableProducts(ShoppingCartDto shoppingCartDto);

    void addQuantity(AddProductToWarehouseRequest request);

    AddressDto getAddressWarehouse();
}
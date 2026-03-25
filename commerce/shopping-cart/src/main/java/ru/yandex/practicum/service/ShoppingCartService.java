package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProduct(String username, Map<UUID, Long> products);

    void deactivateCart(String username);

    ShoppingCartDto removeProducts(String username, List<UUID> products);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);

    BookedProductsDto reserveProducts(String nameUser);
}
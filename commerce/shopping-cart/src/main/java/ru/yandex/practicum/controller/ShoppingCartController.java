package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.feings.ShoppingCartFeignClient;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartFeignClient {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@Valid @NotBlank @RequestParam String username) {
        log.info("[GET] Получение корзины полтзователя {}", username);
        return shoppingCartService.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProduct(@RequestParam String username,
                                      @RequestBody Map<UUID, Long> products) {
        log.info("[PUT] Добавление товара {}, в корзину {}", products, username);
        return shoppingCartService.addProduct(username, products);
    }

    @DeleteMapping
    public void deactivate(@RequestParam String username) {
        log.info("[DELETE] удаление корзины {}", username);
        shoppingCartService.deactivateCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String username, @RequestBody List<UUID> products) {
        log.info("[POST] Удаление товаров: {} в корзине  {}", products, username);
        return shoppingCartService.removeProducts(username, products);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest request) {
        log.info("[POST] Изменить количество  в корзине {}, request: {}", username, request);
        return shoppingCartService.changeQuantity(username, request);
    }

    @PostMapping("/booking")
    public BookedProductsDto reserveProducts(@RequestParam String nameUser) {
        log.info("[POST] Запрос на зарезервирование товаров на складе для пользователя {}", nameUser);
        return shoppingCartService.reserveProducts(nameUser);
    }
}

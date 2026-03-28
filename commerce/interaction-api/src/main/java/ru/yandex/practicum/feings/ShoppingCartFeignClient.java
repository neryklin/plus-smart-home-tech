package ru.yandex.practicum.feings;

import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartFeignClient {
    @GetMapping
    ShoppingCartDto getShoppingCart(@NotBlank @RequestParam String username);

    @PutMapping
    ShoppingCartDto addProduct(@RequestParam @NotBlank String username, @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deactivate(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProducts(@RequestParam String username, @RequestBody List<UUID> products);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request);

    @PostMapping("/booking")
    BookedProductsDto reserveProducts(@RequestParam String nameUser);
}

package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.enums.ShoppingCartState;
import ru.yandex.practicum.exeption.DiferentException;
import ru.yandex.practicum.exeption.NotFoundException;
import ru.yandex.practicum.feings.WarehouseProductFeignClient;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseProductFeignClient warehouseProductFeignClient;

    @Override
    @Transactional
    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart shoppingCart = findUserCartorCreate(username);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProduct(String username, Map<UUID, Long> products) {
        ShoppingCart shoppingCart = findUserCartorCreate(username);

        warehouseProductFeignClient.checkAvailableProducts(new ShoppingCartDto(shoppingCart.getShoppingCartId(), products));

        shoppingCart.getProducts().putAll(products);
        ShoppingCart addShoppingCart = shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toShoppingCartDto(addShoppingCart);
    }

    @Override
    @Transactional
    public void deactivateCart(String username) {
        ShoppingCart shoppingCart = getCart(username);
        shoppingCart.setState(ShoppingCartState.DEACTIVATE);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String username, Map<UUID, Long> products) {
        ShoppingCart shoppingCart = getCart(username);
        shoppingCart.setProducts(products);
        return shoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    @Transactional
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart shoppingCart = getCart(username);
        if (shoppingCart.getState() == ShoppingCartState.DEACTIVATE) {
            throw new DiferentException("ошибка доабвления товара - корзина деактиврована");
        }
        if (!shoppingCart.getProducts().containsKey(request.getProductId())) {
            throw new DiferentException("ошибка добавленеия товара");
        }
        shoppingCart.getProducts().put(request.getProductId(), request.getNewQuantity());
        return shoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public BookedProductsDto reserveProducts(String nameUser) {
        ShoppingCart shoppingCart = findUserCartorCreate(nameUser);
        return warehouseProductFeignClient.checkAvailableProducts(shoppingCartMapper.toShoppingCartDto(shoppingCart));
    }

    private ShoppingCart findUserCartorCreate(String username) {
        checkUserPresence(username);
        return shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> createNewShoppingCart(username));
    }

    private ShoppingCart getCart(String username) {
        checkUserPresence(username);
        return shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("нет корзины у " + username));
    }

    private ShoppingCart createNewShoppingCart(String username) {
        ShoppingCart newShoppingCart = ShoppingCart.builder()
                .shoppingCartId(UUID.randomUUID())
                .username(username)
                .products(new HashMap<>())
                .state(ShoppingCartState.ACTIVE)
                .build();
        return shoppingCartRepository.save(newShoppingCart);
    }

    private void checkUserPresence(String username) {
        if (username == null || username.isEmpty())
            throw new DiferentException("Отсутствует пользователь");
    }
}
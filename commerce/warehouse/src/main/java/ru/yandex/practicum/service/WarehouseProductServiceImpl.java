package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exeption.DiferentException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseProductServiceImpl implements WarehouseProductService {
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];
    private final WarehouseProductRepository warehouseProductRepository;
    private final WarehouseProductMapper warehouseProductMapper;

    @Override
    @Transactional
    public void newProduct(NewProductInWarehouseRequest request) {
        Optional<WarehouseProduct> product = warehouseProductRepository.findById(request.getProductId());
        if (product.isPresent()) {
            throw new DiferentException(" данный товар уже есть" + request.getProductId());
        }
        WarehouseProduct warehouseProduct = warehouseProductRepository.save(warehouseProductMapper.mapToWarehouseProduct(request));
        log.info("создали товар: {}", warehouseProduct);
    }

    @Override
    @Transactional
    public BookedProductsDto checkAvailableProducts(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> cartProducts = shoppingCartDto.getProducts();
        Map<UUID, WarehouseProduct> products = warehouseProductRepository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        if (products.size() != cartProducts.size()) {
            throw new DiferentException("товары отсутвуют на складе");
        }

        double deliveryWeight = warehouseProductRepository.findAll().stream()
                .map(WarehouseProduct::getWeight)
                .mapToDouble(Double::doubleValue)
                .sum();


        double deliveryVolume = warehouseProductRepository.findAll().stream()
                .map(warehouseProduct ->
                        warehouseProduct.getDimension().getDepth() *
                                warehouseProduct.getDimension().getHeight() *
                                warehouseProduct.getDimension().getWidth())
                .mapToDouble(Double::doubleValue)
                .sum();

        boolean fragile = warehouseProductRepository.findAll().stream()
                .anyMatch(WarehouseProduct::isFragile);


        return new BookedProductsDto(deliveryWeight, deliveryVolume, fragile);
    }

    @Override
    @Transactional
    public void addQuantity(AddProductToWarehouseRequest request) {

        Optional<WarehouseProduct> product = warehouseProductRepository.findById(request.getProductId());
        if (product.isEmpty()) {
            log.error("Товара нет на складе: {}", request.getProductId());
            throw new DiferentException("товара нет на складе");
        }
        WarehouseProduct warehouseProduct = product.get();
        warehouseProduct.setQuantity(warehouseProduct.getQuantity() + request.getQuantity());
        WarehouseProduct updatedProduct = warehouseProductRepository.save(warehouseProduct);
        log.info("опдейт количества : {}", updatedProduct);
    }

    @Override
    public AddressDto getAddressWarehouse() {
        return AddressDto.builder()
                .country("гондурас")
                .city("город в гондукрасе")
                .street("пальмовая")
                .house("1")
                .flat("1")
                .build();
    }
}
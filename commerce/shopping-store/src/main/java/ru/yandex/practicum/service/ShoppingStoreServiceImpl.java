package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.exeption.NotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ShoppingStoreRepository shoppingStoreRepository;
    private final ProductMapper productMapper;


    private Product findProduct(UUID productId) {
        return shoppingStoreRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Товар {} не найден", productId);
                    return new NotFoundException(String.format("Товар = %s не найден", productId));
                });
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {
        Product product = shoppingStoreRepository.save(productMapper.ToProduct(productDto));
        log.info("создан Товар : {}", product);
        return productMapper.ToProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto update(ProductDto productDto) {
        findProduct(productDto.getProductId());
        log.info("обновлен Товар : {}", productDto);
        return productMapper.ToProductDto(shoppingStoreRepository.save(productMapper.ToProduct(productDto)));
    }

    @Override
    @Transactional
    public boolean removedProductById(UUID productId) {
        Product product = findProduct(productId);
        product.setProductState(ProductState.DEACTIVATE);
        shoppingStoreRepository.save(product);
        log.info("удален Товар {}", productId);
        return true;
    }

    @Override
    @Transactional
    public boolean setQuantity(UUID productId, QuantityState state) {
        Product product = findProduct(productId);
        product.setQuantityState(state);
        shoppingStoreRepository.save(product);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> get(ProductCategory category, Pageable pageable) {
        Page<Product> productsPage = shoppingStoreRepository.findAllByProductCategory(category, pageable);
        return productsPage.map(productMapper::ToProductDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        return productMapper.ToProductDto(findProduct(productId));
    }


}

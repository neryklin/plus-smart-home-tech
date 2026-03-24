package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto ToProductDto(Product product);

    Product ToProduct(ProductDto productDto);
}
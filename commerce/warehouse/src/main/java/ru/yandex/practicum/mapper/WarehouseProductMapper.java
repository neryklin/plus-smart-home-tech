package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.WarehouseProduct;


@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {
    WarehouseProduct mapToWarehouseProduct(NewProductInWarehouseRequest request);
}
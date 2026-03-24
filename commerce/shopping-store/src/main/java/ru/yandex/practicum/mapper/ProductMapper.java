package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.Product;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductDto ToProductDto(Product product);

  //  @Mapping(target = "product_Id", source = "productId")
//    @Mapping(target = "productName", source = "productName")
//    @Mapping(target = "description", source = "description")
//    @Mapping(target = "imageSrc", source = "imageSrc")
//    @Mapping(target = "quantity_state", source = "quantityState")
//    @Mapping(target = "product_state", source = "productState")
//    @Mapping(target = "product_category", source = "productCategory")
//    @Mapping(target = "price", source = "price")
    Product ToProduct(ProductDto productDto);
}
package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.Product;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductDto ToProductDto(Product product);

    Product ToProduct(ProductDto productDto);

    default ProductDto toDto(Product product) {
            if (product == null) return null;

            ProductDto dto = new ProductDto();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setDescription(product.getDescription());
            dto.setImageSrc(product.getImageSrc());
            dto.setQuantityState(product.getQuantityState());
            dto.setProductState(product.getProductState());
            dto.setProductCategory(product.getProductCategory());
            dto.setPrice(product.getPrice());
            return dto;
        }

    default Product toEntity(ProductDto dto) {
            if (dto == null) return null;
            Product product = new Product();
             product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setImageSrc(dto.getImageSrc());
        product.setQuantityState(dto.getQuantityState());
        product.setProductState(dto.getProductState());
        product.setProductCategory(dto.getProductCategory());
        product.setPrice(dto.getPrice());
        return product;
        }


}
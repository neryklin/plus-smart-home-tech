package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.*;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
@ToString
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(name = "product_name")
    private String productName;

    @Column
    private String description;

    @Column
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state")
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state")
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category")
    private ProductCategory productCategory;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal price;
}
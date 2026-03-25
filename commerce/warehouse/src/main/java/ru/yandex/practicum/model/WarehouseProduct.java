package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "warehouse_product")
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Embedded
    private Size dimension;

    @Column
    private boolean fragile;

    @Column(name = "quantity", nullable = false)
    private Long quantity = 0L;
}
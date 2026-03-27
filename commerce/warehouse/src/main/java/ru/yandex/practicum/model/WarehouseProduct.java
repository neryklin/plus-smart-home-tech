package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
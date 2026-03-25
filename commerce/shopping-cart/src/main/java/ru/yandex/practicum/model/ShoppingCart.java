package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.ShoppingCartState;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "carts")
@Getter
@Setter
public class ShoppingCart {
    @Id
    @Column(name = "cart_id", nullable = false)
    private UUID shoppingCartId;

    @Column(name = "username", nullable = false)
    private String username;

    @ElementCollection
    @CollectionTable(
            name = "cart_products",
            joinColumns = @JoinColumn(name = "cart_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products;

    @Enumerated(EnumType.STRING)
    private ShoppingCartState state;
}
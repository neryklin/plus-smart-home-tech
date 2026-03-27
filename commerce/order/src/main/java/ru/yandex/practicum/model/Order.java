package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.OrderState;

import java.util.Map;
import java.util.UUID;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    private String username;

    @Column(name = "cart_id")
    private UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products;

    private UUID paymentId;

    @Column()
    private double deliveryVolume;

    private boolean fragile;

    @Column()
    private double totalPrice;

    @Column()
    private double deliveryPrice;

    @Column()
    private double productPrice;

    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @Column()
    private double deliveryWeight;

}
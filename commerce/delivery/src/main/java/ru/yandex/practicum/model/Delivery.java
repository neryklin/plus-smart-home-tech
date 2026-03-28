package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.DeliveryState;

import java.util.UUID;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryId;

    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private DeliveryState state;

    @ManyToOne
    @JoinColumn(name = "from_address_id", nullable = false)
    private Address fromAddress;

    @ManyToOne
    @JoinColumn(name = "to_address_id", nullable = false)
    private Address toAddress;

}
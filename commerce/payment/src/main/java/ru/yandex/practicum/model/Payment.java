package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.PaymentState;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    private UUID orderId;

    @Column()
    private Double totalPayment;

    @Column()
    private Double deliveryTotal;

    @Column()
    private Double feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentState state;
}
package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.enums.OrderState;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID orderId;
    private UUID shoppingCartId;
    @NotNull
    private Map<UUID, Long> products;
    private UUID paymentId;
    private UUID deliveryId;
    private OrderState state;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private boolean fragile;
    private Double totalPrice;
    private Double deliveryPrice;
    private Double productPrice;
}
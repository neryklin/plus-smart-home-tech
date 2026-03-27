package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createNewDelivery(DeliveryDto deliveryDto);

    void setDeliverySuccess(UUID deliveryId);

    void setDeliveryPicked(UUID deliveryId);

    void setDeliveryError(UUID deliveryId);

    Double getCostDelivery(OrderDto orderDto);
}
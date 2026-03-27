package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto);

    Double getTotalPayment(OrderDto orderDto);

    Double getProductPayment(OrderDto orderDto);

    void refundPayment(UUID paymentId);

    void PaymentError(UUID paymentId);
}

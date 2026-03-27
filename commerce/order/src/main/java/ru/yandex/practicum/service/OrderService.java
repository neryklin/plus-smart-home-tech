package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequest;

import java.util.UUID;

public interface OrderService {
    OrderDto paymentOrder(UUID orderId);

    OrderDto paymentOrderError(UUID orderId);

    OrderDto calculateTotalOrder(UUID orderId);

    OrderDto calculateTotalDeliveryOrder(UUID orderId);

    OrderDto assemblyOrder(UUID orderId);

    OrderDto assemblyOrderError(UUID orderId);

    Page<OrderDto> getUserOrders(String username, Pageable pageable);

    OrderDto createNewOrder(CreateNewOrderRequest request);

    OrderDto returnOrder(ProductReturnRequest returnRequest);

    OrderDto deliveryOrder(UUID orderId);

    OrderDto deliveryOrderError(UUID orderId);

    OrderDto completedOrder(UUID orderId);

}
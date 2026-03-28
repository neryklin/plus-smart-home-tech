package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequest;
import ru.yandex.practicum.feings.OrderFeignClient;
import ru.yandex.practicum.service.OrderService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController implements OrderFeignClient {
    private final OrderService orderService;

    @PostMapping("/payment")
    public OrderDto paymentOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Оплата заказа {} ", orderId);
        return orderService.paymentOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto PaymentOrderError(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Оплата заказа ошибка {} ", orderId);
        return orderService.paymentOrderError(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] стоимость {} ", orderId);
        return orderService.calculateTotalOrder(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateTotalDeliveryOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Рстоиомсть доставки {} ", orderId);
        return orderService.calculateTotalDeliveryOrder(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] сборка заказа {} ", orderId);
        return orderService.assemblyOrder(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyOrderError(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] сборка заказа ошибка {} ", orderId);
        return orderService.assemblyOrderError(orderId);
    }

    @GetMapping
    public Page<OrderDto> getUserOrders(@RequestParam String username, Pageable pageable) {
        log.info("[GET] cписок заказов {} постаранично {} ", username, pageable);
        return orderService.getUserOrders(username, pageable);
    }

    @PutMapping
    public OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request) {
        log.info("[PUT] новый заказ {} ", request);
        return orderService.createNewOrder(request);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@RequestBody ProductReturnRequest returnRequest) {
        log.info("[POST] Возврат заказа {} ", returnRequest);
        return orderService.returnOrder(returnRequest);
    }


    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Доставка заказа {} ", orderId);
        return orderService.deliveryOrder(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryOrderError(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Доставка заказа ошибка {} ", orderId);
        return orderService.deliveryOrderError(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] заказ завершен {} ", orderId);
        return orderService.completedOrder(orderId);
    }

}
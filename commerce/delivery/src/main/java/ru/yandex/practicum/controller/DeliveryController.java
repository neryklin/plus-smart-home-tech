package ru.yandex.practicum.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.feings.DeliveryFeignClient;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Validated
public class DeliveryController implements DeliveryFeignClient {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createNewDelivery(@RequestBody DeliveryDto deliveryDto) {
        log.info("[PUT] Новая доставка: {}", deliveryDto);
        return deliveryService.createNewDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    public void setDeliverySuccess(@RequestBody @NotNull UUID deliveryId) {
        log.info("[POST] доставлено: {}", deliveryId);
        deliveryService.setDeliverySuccess(deliveryId);
    }

    @PostMapping("/picked")
    public void setDeliveryPicked(@RequestBody @NotNull UUID deliveryId) {
        log.info("[POST] получено: {}", deliveryId);
        deliveryService.setDeliveryPicked(deliveryId);
    }

    @PostMapping("/failed")
    public void setDeliveryError(@RequestBody @NotNull UUID deliveryId) {
        log.info("[POST] фэйл доставки: {}", deliveryId);
        deliveryService.setDeliveryError(deliveryId);
    }

    @PostMapping("/cost")
    public Double getCostDelivery(@RequestBody OrderDto orderDto) {
        log.info("[POST] Расчёт стоимости доставки: {}", orderDto);
        return deliveryService.getCostDelivery(orderDto);
    }
}
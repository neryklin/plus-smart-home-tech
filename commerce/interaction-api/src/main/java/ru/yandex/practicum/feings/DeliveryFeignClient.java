package ru.yandex.practicum.feings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient {
    @PutMapping
    DeliveryDto createNewDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void setDeliverySuccess(@RequestBody @NotNull UUID deliveryId);

    @PostMapping("/picked")
    void setDeliveryPicked(@RequestBody @NotNull UUID deliveryId);

    @PostMapping("/failed")
    void setDeliveryError(@RequestBody @NotNull UUID deliveryId);

    @PostMapping("/cost")
    Double getCostDelivery(@RequestBody @Valid OrderDto orderDto);
}

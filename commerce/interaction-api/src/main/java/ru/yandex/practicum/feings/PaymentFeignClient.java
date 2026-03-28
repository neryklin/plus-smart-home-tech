package ru.yandex.practicum.feings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient {
    @PostMapping
    PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/totalCost")
    Double getTotalPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/productCost")
    Double getProductPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/refund")
    void refundPayment(@RequestBody @NotNull UUID paymentId);

    @PostMapping("/failed")
    void PaymentError(@RequestBody @NotNull UUID paymentId);
}
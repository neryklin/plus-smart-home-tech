package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.feings.PaymentFeignClient;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController implements PaymentFeignClient {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPayment(@RequestBody OrderDto orderDto) {
        log.info("[POST]  для оплаты {}", orderDto);
        return paymentService.createPayment(orderDto);
    }

    @PostMapping("/totalCost")
    public Double getTotalPayment(@RequestBody OrderDto orderDto) {
        log.info("[POST] стоимомть заказа {}", orderDto);
        return paymentService.getTotalPayment(orderDto);
    }

    @PostMapping("/productCost")
    public Double getProductPayment(@Valid @RequestBody OrderDto orderDto) {
        log.info("[POST] стоимость товара {}", orderDto);
        return paymentService.getProductPayment(orderDto);
    }

    @PostMapping("/refund")
    public void refundPayment(@RequestBody @NotNull UUID paymentId) {
        log.info("[POST] акцепт платежа {}", paymentId);
        paymentService.refundPayment(paymentId);
    }

    @PostMapping("/failed")
    public void PaymentError(@RequestBody @NotNull UUID paymentId) {
        log.info("[POST] ошибка платежа {}", paymentId);
        paymentService.PaymentError(paymentId);
    }
}
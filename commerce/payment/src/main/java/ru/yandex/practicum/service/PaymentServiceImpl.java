package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exeption.NotFoundException;
import ru.yandex.practicum.feings.OrderFeignClient;
import ru.yandex.practicum.feings.ShoppingStoreFeignClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreFeignClient shoppingStoreFeignClient;
    private final OrderFeignClient orderFeignClient;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(orderDto.getTotalPrice())
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(orderDto.getTotalPrice() * 0.1)
                .state(PaymentState.PENDING)
                .build();
        Payment createdPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentDto(createdPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalPayment(OrderDto orderDto) {
        return orderDto.getProductPrice() + orderDto.getTotalPrice() * 0.1 + orderDto.getDeliveryVolume();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getProductPayment(OrderDto orderDto) {
        Map<UUID, Long> products = orderDto.getProducts();
        if (products == null)
            throw new NotFoundException("нет товаров в заказе");

        return products.entrySet().stream()
                .mapToDouble(product -> {
                    ProductDto productDto = shoppingStoreFeignClient.getProductById(product.getKey());
                    return productDto.getPrice() * product.getValue();
                })
                .sum();
    }

    @Override
    @Transactional
    public void refundPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setState(PaymentState.SUCCESS);
        orderFeignClient.paymentOrder(payment.getOrderId());
        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void PaymentError(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setState(PaymentState.FAILED);
        orderFeignClient.PaymentOrderError(payment.getOrderId());
        paymentRepository.save(payment);
    }


    private Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платёж не найден " + paymentId));
    }
}
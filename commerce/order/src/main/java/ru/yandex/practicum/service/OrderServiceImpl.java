package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exeption.NotFoundException;
import ru.yandex.practicum.feings.DeliveryFeignClient;
import ru.yandex.practicum.feings.PaymentFeignClient;
import ru.yandex.practicum.feings.WarehouseProductFeignClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseProductFeignClient warehouseProductFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;

    @Override
    public Page<OrderDto> getUserOrders(String username, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUsername(username, pageable);
        return orders.map(orderMapper::toOrderDto);
    }

    private OrderDto setState(UUID orderId, OrderState newState) {
        Order order = getOrder(orderId);
        order.setState(newState);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден" + orderId));
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest createNewOrderRequest) {
        BookedProductsDto bookedProductsDto = warehouseProductFeignClient.reserveProducts(createNewOrderRequest.getShoppingCart());

        Order order = Order.builder()
                .shoppingCartId(createNewOrderRequest.getShoppingCart().getShoppingCartId())
                .products(createNewOrderRequest.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .deliveryWeight(bookedProductsDto.getDeliveryWeight())
                .deliveryVolume(bookedProductsDto.getDeliveryVolume())
                .fragile(bookedProductsDto.isFragile())
                .build();
        ;
        orderRepository.save(order);

        AddressDto warehouseAddress = warehouseProductFeignClient.getAddressWarehouse();
        DeliveryDto newDelivery = DeliveryDto.builder()
                .orderId(order.getOrderId())
                .fromAddress(warehouseAddress)
                .toAddress(warehouseAddress)
                .state(DeliveryState.CREATED)
                .build();
        order.setDeliveryId(newDelivery.getDeliveryId());

        PaymentDto newPayment = paymentFeignClient.createPayment(orderMapper.toOrderDto(order));
        order.setPaymentId(newPayment.getPaymentId());
        order.setTotalPrice(newPayment.getTotalPayment());
        order.setDeliveryPrice(newPayment.getDeliveryTotal());
        Double productPrice = paymentFeignClient.getProductPayment(orderMapper.toOrderDto(order));
        order.setProductPrice(productPrice);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDto(savedOrder);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) {
        warehouseProductFeignClient.returnProducts(productReturnRequest.getProducts());
        return setState(productReturnRequest.getOrderId(), OrderState.PRODUCT_RETURNED);
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) {

        return setState(orderId, OrderState.PAID);
    }

    @Override
    public OrderDto paymentOrderError(UUID orderId) {

        return setState(orderId, OrderState.PAYMENT_FAILED);
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {

        return setState(orderId, OrderState.DELIVERED);
    }

    @Override
    public OrderDto deliveryOrderError(UUID orderId) {

        return setState(orderId, OrderState.DELIVERY_FAILED);
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {

        return setState(orderId, OrderState.COMPLETED);
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {

        return setState(orderId, OrderState.ASSEMBLED);
    }

    @Override
    public OrderDto assemblyOrderError(UUID orderId) {

        return setState(orderId, OrderState.ASSEMBLY_FAILED);
    }

    @Override
    public OrderDto calculateTotalOrder(UUID orderId) {
        Order order = getOrder(orderId);
        Double totalPrice = paymentFeignClient.getTotalPayment(orderMapper.toOrderDto(order));
        order.setTotalPrice(totalPrice);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateTotalDeliveryOrder(UUID orderId) {
        Order order = getOrder(orderId);
        Double deliveryPrice = deliveryFeignClient.getCostDelivery(orderMapper.toOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }


}
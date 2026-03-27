package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShipperToDeliveryRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exeption.NotFoundException;
import ru.yandex.practicum.feings.OrderFeignClient;
import ru.yandex.practicum.feings.WarehouseProductFeignClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderFeignClient orderFeignClient;
    private final WarehouseProductFeignClient warehouseProductFeignClient;


    @Override
    @Transactional
    public DeliveryDto createNewDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);
        Delivery savedDelivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDeliveryDto(savedDelivery);
    }

    @Override
    @Transactional
    public void setDeliverySuccess(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderFeignClient.deliveryOrder(delivery.getOrderId());
    }

    @Override
    @Transactional
    public void setDeliveryPicked(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        UUID orderId = delivery.getOrderId();
        delivery.setState(DeliveryState.IN_PROGRESS);
        orderFeignClient.assemblyOrder(orderId);
        warehouseProductFeignClient.shippedToDelivery(new ShipperToDeliveryRequest(orderId, deliveryId));
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void setDeliveryError(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        delivery.setState(DeliveryState.FAILED);
        orderFeignClient.deliveryOrderError(delivery.getOrderId());
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getCostDelivery(OrderDto orderDto) {
        Delivery delivery = findDeliveryOrThrow(orderDto.getDeliveryId());
        Address from = delivery.getFromAddress();
        Address to = delivery.getToAddress();
        double cost = 5;
        if (from.getStreet().contains("ADDRESS_2")) {
            cost += cost * 2;
        }
        if (orderDto.isFragile()) {
            cost += cost * 0.2;
        }
        cost += orderDto.getDeliveryWeight().doubleValue() * 0.3;
        cost += orderDto.getDeliveryVolume().doubleValue() * 0.2;
        if (!from.getStreet().equalsIgnoreCase(to.getStreet())) {
            cost += cost * 0.2;
        }
        return Double.valueOf(cost);
    }

    private Delivery findDeliveryOrThrow(UUID deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new NotFoundException("Доставка не найдена: " + deliveryId));
    }
}
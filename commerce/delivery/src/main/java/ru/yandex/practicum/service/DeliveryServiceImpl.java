package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Transactional
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderFeignClient orderFeignClient;
    private final WarehouseProductFeignClient warehouseProductFeignClient;


    @Override
    public DeliveryDto createNewDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);
        Delivery savedDelivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDeliveryDto(savedDelivery);
    }

    @Override
    public void setDeliverySuccess(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderFeignClient.deliveryOrder(delivery.getOrderId());
    }

    @Override
    public void setDeliveryPicked(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        UUID orderId = delivery.getOrderId();
        delivery.setState(DeliveryState.IN_PROGRESS);
        orderFeignClient.assemblyOrder(orderId);
        warehouseProductFeignClient.shippedToDelivery(new ShipperToDeliveryRequest(orderId, deliveryId));
        deliveryRepository.save(delivery);
    }

    @Override
    public void setDeliveryError(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        delivery.setState(DeliveryState.FAILED);
        orderFeignClient.deliveryOrderError(delivery.getOrderId());
        deliveryRepository.save(delivery);
    }

    @Override
    public Double getCostDelivery(OrderDto orderDto) {
        log.info("старт рассчет стоимость доставки  : {}", orderDto.getOrderId());
        Delivery delivery = findDeliveryOrThrow(orderDto.getDeliveryId());
        Address from = delivery.getFromAddress();
        Address to = delivery.getToAddress();
        double cost = 5;
        if (from.getStreet().contains("ADDRESS_2")) {
            cost += cost * 2;
            log.debug(" отладочные сообщения from.getStreet().contains(ADDRESS_ : {}", cost);
        }
        if (orderDto.isFragile()) {
            cost += cost * 0.2;
            log.debug(" отладочные сообщения forderDto.isFragile(): {}", cost);
        }
        cost += orderDto.getDeliveryWeight().doubleValue() * 0.3;
        log.debug(" отладочные сообщения cost += orderDto.getDeliveryWeight().doubleValue() * 0.3: {}", cost);
        cost += orderDto.getDeliveryVolume().doubleValue() * 0.2;
        log.debug(" отладочные сообщения cost += orderDto.getDeliveryVolume().doubleValue() * 0.2 : {}", cost);
        if (!from.getStreet().equalsIgnoreCase(to.getStreet())) {
            cost += cost * 0.2;
            log.debug(" отладочные сообщения !from.getStreet().equalsIgnoreCase(to.getStreet()) : {}", cost);
        }
        log.info("окончание рассчет стоимость доставки  : {}", orderDto.getOrderId());
        return Double.valueOf(cost);
    }

    private Delivery findDeliveryOrThrow(UUID deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new NotFoundException("Доставка не найдена: " + deliveryId));
    }
}
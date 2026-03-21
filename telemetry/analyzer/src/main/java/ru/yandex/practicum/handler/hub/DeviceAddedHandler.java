package ru.yandex.practicum.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.mapper.Mapper;
import ru.yandex.practicum.repository.SensorRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class DeviceAddedHandler implements HubEventHandler {
    final SensorRepository sensorRepository;
    final Mapper mapper;


    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        sensorRepository.save(mapper.mapToSensor(event));
        log.info("{Сохраняем в БД: {}", event.getHubId());

    }

    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }
}
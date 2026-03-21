package ru.yandex.practicum.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class DeviceRemovedHandler implements HubEventHandler {
    final SensorRepository sensorRepository;


    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro deviceRemovedAvro = (DeviceRemovedEventAvro) event.getPayload();
        sensorRepository.findByIdAndHubId(deviceRemovedAvro.getId(), event.getHubId());
        sensorRepository.deleteById(deviceRemovedAvro.getId());
        log.info("Sensor c id={}, удаляем", deviceRemovedAvro.getId());
    }

    @Override
    public String getType() {

        return DeviceRemovedHandler.class.getSimpleName();
    }
}

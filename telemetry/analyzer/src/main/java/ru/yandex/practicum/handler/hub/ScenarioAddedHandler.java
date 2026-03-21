package ru.yandex.practicum.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.mapper.Mapper;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedHandler implements HubEventHandler {
    final ActionRepository actionRepository;
    final ConditionRepository conditionRepository;
    final ScenarioRepository scenarioRepository;
    final SensorRepository sensorRepository;
    final Mapper mapper;


    private Boolean checkSensorInConditionsRepository(ScenarioAddedEventAvro event, String hubId) {
        List<String> sensorIds = event.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList();
        return sensorRepository.existsByIdInAndHubId(sensorIds, hubId);
    }

    private Boolean checkSensorInActionsRepository(ScenarioAddedEventAvro event, String hubId) {
        List<String> sensorIds = event.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .toList();
        return sensorRepository.existsByIdInAndHubId(sensorIds, hubId);
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedAvro = (ScenarioAddedEventAvro) event.getPayload();
        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(event.getHubId(),
                scenarioAddedAvro.getName());

        Scenario scenarioEntity = scenario.orElseGet(() -> scenarioRepository.save(mapper.mapToScenario(event)));

        if (checkSensorInActionsRepository(scenarioAddedAvro, event.getHubId())) {
            actionRepository.saveAll(mapper.mapToActions(scenarioAddedAvro, scenarioEntity));
            log.info("{Сохраняем в БД scenario: {}", scenario);
        }
        if (checkSensorInConditionsRepository(scenarioAddedAvro, event.getHubId())) {
            conditionRepository.saveAll(mapper.mapToConditions(scenarioAddedAvro, scenarioEntity));
            log.info("{Сохраняем в БД scenario: {}", scenario);
        }
    }

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }
}

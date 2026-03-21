package ru.yandex.practicum.handler.snapshot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.HubRouterProcessor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotHandler {
    final HubRouterProcessor hubRouterClient;
    final ConditionRepository conditionRepository;
    final ActionRepository actionRepository;
    final ScenarioRepository scenarioRepository;

    private void sendAction(Scenario scenario) {
        actionRepository.findAllByScenario(scenario).forEach(hubRouterClient::executeAction);
    }

    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        Map<String, SensorStateAvro> sensorState = snapshot.getSensorsState();
        scenarioRepository.findByHubId(snapshot.getHubId()).stream()
                .filter(scenario -> checkScenario(scenario, sensorState))
                .forEach(scenario -> {
                    sendAction(scenario);
                });
    }

    private Boolean checkValue(Condition condition, int currentValue, int conditionValue) {
        return switch (condition.getOperation()) {
            case EQUALS -> currentValue == conditionValue;
            case GREATER_THAN -> currentValue > conditionValue;
            case LOWER_THAN -> currentValue < conditionValue;
        };
    }

    private Boolean checkCondition(Condition condition, Map<String, SensorStateAvro> sensorState) {
        SensorStateAvro sensorStateAvro = sensorState.get(condition.getSensor().getId());

        if (sensorStateAvro == null) {
            return false;
        }
        Object data = sensorStateAvro.getData();
        int currentValue = switch (condition.getType()) {
            case TEMPERATURE -> ((ClimateSensorAvro) data).getTemperatureC();
            case HUMIDITY -> ((ClimateSensorAvro) data).getHumidity();
            case LUMINOSITY -> ((LightSensorAvro) data).getLuminosity();
            case CO2LEVEL -> ((ClimateSensorAvro) data).getCo2Level();
            case SWITCH -> ((SwitchSensorAvro) data).getState() ? 1 : 0;
            case MOTION -> ((MotionSensorAvro) data).getMotion() ? 1 : 0;
        };
        return checkValue(condition, currentValue, condition.getValue());
    }

    private Boolean checkScenario(Scenario scenario, Map<String, SensorStateAvro> sensorState) {
        return conditionRepository.findAllByScenario(scenario).stream()
                .allMatch(condition -> checkCondition(condition, sensorState));
    }

}
package ru.yandex.practicum.model.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mapper {
    final SensorRepository sensorRepository;

    public Mapper(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    private Integer mapValue(Object value) {
        return switch (value) {
            case null -> null;
            case Boolean b -> (b ? 1 : 0);
            case Integer i -> i;
            default -> throw new ClassCastException("Ошибка преобразования");
        };
    }

    public Set<Condition> mapToConditions(ScenarioAddedEventAvro event, Scenario scenario) {
        return event.getConditions().stream()
                .map(condition -> Condition.builder()
                        .sensor(sensorRepository.findById(condition.getSensorId()).orElseThrow())
                        .scenario(scenario)
                        .type(condition.getType())
                        .operation(condition.getOperation())
                        .value(mapValue(condition.getValue()))
                        .build())
                .collect(Collectors.toSet());
    }

    public Set<Action> mapToActions(ScenarioAddedEventAvro event, Scenario scenario) {
        return event.getActions().stream()
                .map(action -> Action.builder()
                        .sensor(sensorRepository.findById(action.getSensorId()).orElseThrow())
                        .scenario(scenario)
                        .type(action.getType())
                        .value(action.getValue())
                        .build())
                .collect(Collectors.toSet());
    }

    public Scenario mapToScenario(HubEventAvro event) {
        ScenarioAddedEventAvro scenario = (ScenarioAddedEventAvro) event.getPayload();
        return Scenario.builder()
                .name(scenario.getName())
                .hubId(event.getHubId())
                .build();
    }

    public DeviceActionRequest MapToActionRequest(Action action) {
        return DeviceActionRequest.newBuilder()
                .setHubId(action.getScenario().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(MapToDeviceActionProto(action))
                .setTimestamp(mapToTimestamp())
                .build();
    }

    private Timestamp mapToTimestamp() {
        Instant instant = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private DeviceActionProto MapToDeviceActionProto(Action action) {
        return DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(mapToActionTypeProto(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    private ActionTypeProto mapToActionTypeProto(ActionTypeAvro actionType) {
        return switch (actionType) {
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
        };
    }

    public Sensor mapToSensor(HubEventAvro event) {
        DeviceAddedEventAvro deviceAddedEvent = (DeviceAddedEventAvro) event.getPayload();
        return Sensor.builder()
                .id(deviceAddedEvent.getId())
                .hubId(event.getHubId())
                .build();
    }


}
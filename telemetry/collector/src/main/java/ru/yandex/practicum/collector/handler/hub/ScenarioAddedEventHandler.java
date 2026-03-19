package ru.yandex.practicum.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {

        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubEventAvro handle(HubEventProto event) {
        ScenarioAddedEventProto eventProto = event.getScenarioAdded();

        List<DeviceActionAvro> deviceActionAvroList = eventProto.getActionList().stream()
                .map(this::mapDeviceAction)
                .toList();
        List<ScenarioConditionAvro> scenarioConditionAvroList = eventProto.getConditionList().stream()
                .map(this::mapScenarioCondition)
                .toList();

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(
                        ScenarioAddedEventAvro.newBuilder()
                                .setName(eventProto.getName())
                                .setActions(deviceActionAvroList)
                                .setConditions(scenarioConditionAvroList)
                                .build()

                )
                .build();

    }

    private ScenarioConditionAvro mapScenarioCondition(ScenarioConditionProto conditionProto) {
        Object value = null;
        if (conditionProto.getValueCase() == ScenarioConditionProto.ValueCase.INT_VALUE) {
            value = conditionProto.getIntValue();
        } else if (conditionProto.getValueCase() == ScenarioConditionProto.ValueCase.BOOL_VALUE) {
            value = conditionProto.getBoolValue();
        }
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(conditionProto.getSensorId())
                .setType(ConditionTypeAvro.valueOf(conditionProto.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(conditionProto.getOperation().name()))
                .setValue(value)
                .build();
    }

    private DeviceActionAvro mapDeviceAction(DeviceActionProto actionProto) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(actionProto.getSensorId())
                .setValue(actionProto.getValue())
                .setType(ActionTypeAvro.valueOf(actionProto.getType().name()))
                .build();
    }


}

//package ru.yandex.practicum.collector.mapper;
//
//import org.apache.avro.specific.SpecificRecordBase;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.collector.model.hubEvent.*;
//import ru.yandex.practicum.kafka.telemetry.event.*;
//
//@Component
//public class HubEventMapper {
//    private static ScenarioConditionAvro mapperScenarioCondition(ScenarioCondition condition) {
//        return ScenarioConditionAvro.newBuilder()
//                .setSensorId(condition.getSensorId())
//                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
//                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
//                .setValue(condition.getValue())
//                .build();
//    }
//
//    private static DeviceActionAvro mapperDeviceAction(DeviceAction action) {
//        return DeviceActionAvro.newBuilder()
//                .setSensorId(action.getSensorId())
//                .setType(ActionTypeAvro.valueOf(action.getType().name()))
//                .setValue(action.getValue())
//                .build();
//    }
//
//    public HubEventAvro toHubEventAvro(HubEvent event) {
//        return HubEventAvro.newBuilder()
//                .setHubId(event.getHubId())
//                .setTimestamp(event.getTimestamp())
//                .setPayload(mapPayload(event))
//                .build();
//    }
//
//    private DeviceAddedEventAvro mapperDeviceAddedEvent(DeviceAddEvent event) {
//        return DeviceAddedEventAvro.newBuilder()
//                .setId(event.getId())
//                .setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
//                .build();
//    }
//
//    private DeviceRemovedEventAvro mapperDeviceRemovedEvent(DeviceRemoveEvent event) {
//        return DeviceRemovedEventAvro.newBuilder()
//                .setId(event.getId())
//                .build();
//    }
//
//    private ScenarioRemovedEventAvro mapperScenarioRemovedEvent(ScenarioRemovEvent event) {
//        return ScenarioRemovedEventAvro.newBuilder()
//                .setName(event.getName())
//                .build();
//    }
//
//    private ScenarioAddedEventAvro mapperScenarioAddedEvent(ScenarioAddEvent event) {
//        return ScenarioAddedEventAvro.newBuilder()
//                .setName(event.getName())
//                .setConditions(event.getConditions()
//                        .stream()
//                        .map(HubEventMapper::mapperScenarioCondition)
//                        .toList())
//                .setActions(event.getActions()
//                        .stream()
//                        .map(HubEventMapper::mapperDeviceAction)
//                        .toList())
//                .build();
//    }
//
//
//    private SpecificRecordBase mapPayload(HubEvent event) {
//        return switch (event.getType()) {
//            case SCENARIO_ADDED -> mapperScenarioAddedEvent((ScenarioAddEvent) event);
//            case SCENARIO_REMOVED -> mapperScenarioRemovedEvent((ScenarioRemovEvent) event);
//            case DEVICE_ADDED -> mapperDeviceAddedEvent((DeviceAddEvent) event);
//            case DEVICE_REMOVED -> mapperDeviceRemovedEvent((DeviceRemoveEvent) event);
//        };
//    }
//
//
//}

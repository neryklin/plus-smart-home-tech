package ru.yandex.practicum.collector.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensorEvent.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
public class SensorEventMapper {
    public SensorEventAvro toSensorEventAvro(SensorEvent event) {
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(mapperPayload(event))
                .build();
    }

    private SpecificRecordBase mapperPayload(SensorEvent event) {
        return switch (event.getType()) {
            case MOTION_SENSOR_EVENT -> mapperMotionSensorEvent((MotionSensorEvent) event);
            case TEMPERATURE_SENSOR_EVENT -> mapperTemperatureSensorEvent((TemperatureSensorEvent) event);
            case SWITCH_SENSOR_EVENT -> mapperSwitchSensorEvent((SwitchSensorEvent) event);
            case LIGHT_SENSOR_EVENT -> mapperLightSensorEvent((LightSensorEvent) event);
            case CLIMATE_SENSOR_EVENT -> mapperClimateSensorEvent((ClimateSensorEvent) event);
        };
    }

    private MotionSensorAvro mapperMotionSensorEvent(MotionSensorEvent event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.getMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    private TemperatureSensorAvro mapperTemperatureSensorEvent(TemperatureSensorEvent event) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }

    private LightSensorAvro mapperLightSensorEvent(LightSensorEvent event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    private ClimateSensorAvro mapperClimateSensorEvent(ClimateSensorEvent event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setCo2Level(event.getCo2Level())
                .setHumidity(event.getHumidity())
                .build();
    }

    private SwitchSensorAvro mapperSwitchSensorEvent(SwitchSensorEvent event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.getState())
                .build();
    }
}
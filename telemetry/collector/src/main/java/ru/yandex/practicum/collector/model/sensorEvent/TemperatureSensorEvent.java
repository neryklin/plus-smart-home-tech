package ru.yandex.practicum.collector.model.sensorEvent;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class TemperatureSensorEvent extends SensorEvent {

    @NotNull
    private int temperatureC;
    @NotNull
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}

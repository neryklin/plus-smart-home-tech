package ru.yandex.practicum.collector.model.hubEvent;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class DeviceRemoveEvent extends HubEvent {

    @NotNull
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}

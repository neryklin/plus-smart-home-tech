package ru.yandex.practicum.collector.model.hubEvent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;


@Getter
@ToString
public class ScenarioAddEvent extends HubEvent {

    @NotBlank
    private String name;

    @NotEmpty
    private ArrayList<ScenarioCondition> conditions;
    @NotEmpty
    private ArrayList<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}

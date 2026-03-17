package ru.yandex.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.collector.model.hubEvent.HubEvent;
import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.collector.service.CollectorService;

@Slf4j
@Controller
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventController {

    private final CollectorService collectorService;

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("controller start sensor event:. " + sensorEvent.toString());

        collectorService.collectSensorEvent(sensorEvent);
        log.info("controller end sensor event:. " + sensorEvent);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("controller start hub event" + hubEvent.toString());
        collectorService.collectHubEvent(hubEvent);
        log.info("controller end hub event" + hubEvent);
    }
}

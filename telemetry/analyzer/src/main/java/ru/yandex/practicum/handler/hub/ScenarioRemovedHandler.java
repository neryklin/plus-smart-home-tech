package ru.yandex.practicum.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioRemovedHandler implements HubEventHandler {
    final ActionRepository actionRepository;
    final ConditionRepository conditionRepository;
    final ScenarioRepository scenarioRepository;


    @Override
    @Transactional
    public void handle(HubEventAvro eventAvro) {
        ScenarioRemovedEventAvro scenarioRemovedAvro = (ScenarioRemovedEventAvro) eventAvro.getPayload();
        Optional<Scenario> currentScenario = scenarioRepository.findByHubIdAndName(eventAvro.getHubId(),
                scenarioRemovedAvro.getName());

        if (currentScenario.isPresent()) {
            Scenario scenarioEntity = currentScenario.get();
            actionRepository.deleteByScenario(scenarioEntity);
            conditionRepository.deleteByScenario(scenarioEntity);
            scenarioRepository.delete(scenarioEntity);
            log.info("Scenario c id={}, был удалён", currentScenario.get());
        } else {
            log.info("Scenario c name={} и hubId={} не был найден", scenarioRemovedAvro.getName(), eventAvro.getHubId());
        }
    }

    @Override
    public String getType() {

        return ScenarioRemovedHandler.class.getSimpleName();
    }
}
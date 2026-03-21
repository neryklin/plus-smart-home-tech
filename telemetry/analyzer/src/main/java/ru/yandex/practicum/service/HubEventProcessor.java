package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.handler.hub.HubEventHandlers;
import ru.yandex.practicum.kafka.ConsumerHubService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubEventProcessor implements Runnable {
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    final ConsumerHubService consumerHub;
    final HubEventHandlers hubHandlers;
    @Value("${kafka.topics.hub}")
    String topic;

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumerHub.consumer::wakeup));
            consumerHub.consumer.subscribe(List.of(topic));
            Map<String, HubEventHandler> hubHandlersMap = hubHandlers.getHandlers();

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> consumerRecords = consumerHub.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (consumerRecords.count() > 0) {
                    for (ConsumerRecord<String, SpecificRecordBase> record : consumerRecords) {
                        HubEventAvro hubEventAvro = (HubEventAvro) record.value();
                        String payloadName = hubEventAvro.getPayload()
                                .getClass().getSimpleName();

                        if (hubHandlersMap.containsKey(payloadName)) {
                            hubHandlersMap.get(payloadName).handle(hubEventAvro);
                        } else {
                            throw new IllegalArgumentException("Хаб. Нет обработчика события: " + hubEventAvro);
                        }
                    }
                    consumerHub.consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.error("Хаб.WakeupException");
        } catch (Exception e) {
            log.error("Хаб.Ошибка обработки ", e);
        } finally {
            log.info("Хаб.Закрываем консьюмер фаинал");
            consumerHub.close();
        }
    }
}
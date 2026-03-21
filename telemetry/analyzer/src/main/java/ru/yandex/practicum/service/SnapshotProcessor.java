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
import ru.yandex.practicum.handler.snapshot.SnapshotHandler;
import ru.yandex.practicum.kafka.ConsumerSnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotProcessor {
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    final ConsumerSnapshotService consumerSnapshot;
    final SnapshotHandler snapshotHandler;
    @Value("${kafka.topics.snapshot}")
    String topic;

    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumerSnapshot.consumer::wakeup));
            consumerSnapshot.consumer.subscribe(List.of(topic));

            while (true) {
                Thread.sleep(2000);
                ConsumerRecords<String, SpecificRecordBase> consumerRecords = consumerSnapshot.poll(CONSUME_ATTEMPT_TIMEOUT);

                if (consumerRecords.count() > 0) {
                    for (ConsumerRecord<String, SpecificRecordBase> record : consumerRecords) {
                        SensorsSnapshotAvro sensorsSnapshot = (SensorsSnapshotAvro) record.value();
                        snapshotHandler.handleSnapshot(sensorsSnapshot);
                    }
                    consumerSnapshot.consumer.commitSync();
                }
            }

        } catch (WakeupException ignored) {
            log.error("снапшот.WakeupException");
        } catch (Exception e) {
            log.error("снапшот. Ошибка обработки", e);
        } finally {
            log.info("Хаб.Закрываем консьюмер фаинал");
            consumerSnapshot.consumer.close();
        }
    }
}
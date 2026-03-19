package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregatorSensorSnapshotServiceImpl implements AggregatorSensorSnapshotService {
    private final Map<String, SensorsSnapshotAvro> sensorsSnapshotMap = new HashMap<>();
    @Value("${collector.kafka.topics.snapshot-events}")
    private String topic;

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {


        SensorsSnapshotAvro currentSnapshot = sensorsSnapshotMap.getOrDefault(
                event.getHubId(),
                addNewSensorsSnapshot(event.getHubId(), event.getTimestamp()));

        SensorStateAvro oldStateSensors = currentSnapshot.getSensorsState().get(event.getId());

        if (oldStateSensors != null) {
            if (oldStateSensors.getTimestamp().isAfter(event.getTimestamp()) ||
                    oldStateSensors.getData().equals(event.getPayload())) {
                log.info("Sensor {} state unchanged", event.getId());
                return Optional.empty();
            }
        }
        log.debug("Sensor {} state changed", event.getId());
        SensorStateAvro newStateSensors = addSensorState(event);
        currentSnapshot.getSensorsState().put(event.getId(), newStateSensors);
        currentSnapshot.setTimestamp(event.getTimestamp());
        sensorsSnapshotMap.put(event.getHubId(), currentSnapshot);
        log.info("Sensor {} state updated", event.getId());
        return Optional.of(currentSnapshot);
    }

    @Override
    public void sendSnapshot(Producer<String, SpecificRecordBase> producer, SensorsSnapshotAvro snapshot) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                snapshot.getHubId(),
                snapshot);

        log.info("Send record: {} \n", record);
        producer.send(record);
    }

    private SensorsSnapshotAvro addNewSensorsSnapshot(String hubId, Instant timestamp) {
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(timestamp)
                .setSensorsState(new HashMap<>())
                .build();
    }

    private SensorStateAvro addSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
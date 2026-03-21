package ru.yandex.practicum.collector.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;


@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorService {
    private final Producer<String, SpecificRecordBase> producer;
    private final String sensorsEventsTopic = "telemetry.sensors.v1";
    private final String hubsEventsTopic = "telemetry.hubs.v1";

    public void collectSensorEvent(SensorEventAvro sensorEvent) {

        log.info("начало обработка события " + sensorEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                sensorsEventsTopic,
                null,
                sensorEvent.getTimestamp().toEpochMilli(),
                sensorEvent.getId(),
                sensorEvent);
        log.info("конец обработка события " + sensorEvent);
        producer.send(record);
    }


    public void collectHubEvent(HubEventAvro hubEvent) {

        log.info("начало обработка хаба] " + hubEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                hubsEventsTopic,
                null,
                hubEvent.getTimestamp().toEpochMilli(),
                hubEvent.getHubId(),
                hubEvent);
        log.info("конец обработка хаба " + hubEvent);
        producer.send(record);
    }

}

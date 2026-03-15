package ru.yandex.practicum.collector.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.mapper.HubEventMapper;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.collector.model.hubEvent.HubEvent;
import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;


@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorService {
    private final Producer<String, SpecificRecordBase> producer;
    private final SensorEventMapper mapperSensor;
    private final HubEventMapper mapperHub;
    private final String sensorsEventsTopic = "telemetry.sensors.v1";
    private final String hubsEventsTopic = "telemetry.hubs.v1";

    public void collectSensorEvent(SensorEvent sensorEvent) {

        log.info("начало обработка события " + sensorEvent.getType());
        SensorEventAvro message = mapperSensor.toSensorEventAvro(sensorEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                sensorsEventsTopic,
                sensorEvent.getId(),
                message);
        log.info("конец обработка события " + sensorEvent.getType());
        producer.send(record);
    }


    public void collectHubEvent(HubEvent hubEvent) {

        log.info("начало обработка хаба] " + hubEvent.getType());
        HubEventAvro message = mapperHub.toHubEventAvro(hubEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                hubsEventsTopic,
                hubEvent.getHubId(),
                message);
        log.info("конец обработка хаба " + hubEvent.getType());
        producer.send(record);
    }

}

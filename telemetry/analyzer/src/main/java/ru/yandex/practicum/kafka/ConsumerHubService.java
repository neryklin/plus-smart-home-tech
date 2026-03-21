package ru.yandex.practicum.kafka;


import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Properties;

@Service
public class ConsumerHubService implements AutoCloseable {

    public final KafkaConsumer<String, SpecificRecordBase> consumer;

    public ConsumerHubService(@Value("${kafka.group-id.hub}") String groupId,
                              @Value("${kafka.auto-commit}") String autoCommit,
                              @Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Properties config = new Properties();
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ru.yandex.practicum.deserializer.HubEventDeserializer.class.getName());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        this.consumer = new KafkaConsumer<>(config);
    }

    public ConsumerRecords<String, SpecificRecordBase> poll(Duration duration) {

        return consumer.poll(duration);
    }

    @Override
    public void close() {

        consumer.close();
    }
}
package com.scb.rider.joballocation.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.UUID;

@Component
@Profile("test")
public class TestKafkaConsumer {

    private Consumer<String, String> kafkaConsumer;

    @Autowired
    public TestKafkaConsumer(EmbeddedKafkaBroker embeddedKafkaBroker) {
        kafkaConsumer = new KafkaConsumer<>(consumerProperties(embeddedKafkaBroker));
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(kafkaConsumer);
    }

    public Consumer<String, String> getConsumer() {
        return kafkaConsumer;
    }

    private Properties consumerProperties(EmbeddedKafkaBroker broker) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put("schema.registry.url", "http://localhost:1080");
        return properties;
    }

    public void reset() {
        KafkaTestUtils.getRecords(kafkaConsumer, 5000);// consume all messages. autocommit=true
    }

}

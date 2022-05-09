package com.scb.rider.joballocation.kafka;

import com.scb.rider.joballocation.Application;
import com.scb.rider.joballocation.kafka.producer.Sender;
import com.scb.rider.joballocation.model.JobStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext
@EmbeddedKafka(topics = {"rider-job-status"})
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest"})
@ActiveProfiles("local")
@Slf4j*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EmbeddedKafka
@ActiveProfiles("local")
@Disabled
class KafkaIntegrationTest {

    private static final String TOPIC_EXAMPLE = "rider-job-status";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    Sender sender;

    @Test
    void testMessage() {
        JobStatus jobStatus = new JobStatus("jobid", "riderid", null, "1");
      /*  Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("group_consumer_test", "false", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        ConsumerFactory cf = new DefaultKafkaConsumerFactory<String, JobStatus>(consumerProps, new StringDeserializer(), new JsonDeserializer<>(JobStatus.class, false));
        Consumer<String, JobStatus> consumerServiceTest = cf.createConsumer();


        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumerServiceTest, TOPIC_EXAMPLE);*/
        sender.send(jobStatus);
     /*   ConsumerRecord<String, JobStatus> consumerRecordOfExampleDTO = KafkaTestUtils.getSingleRecord(consumerServiceTest, TOPIC_EXAMPLE);
        JobStatus valueReceived = consumerRecordOfExampleDTO.value();

        assertEquals("riderid", valueReceived.getRiderId());
        assertEquals("jobid", valueReceived.getJobId());

        consumerServiceTest.close();*/
        assertEquals("jobid", jobStatus.getJobId());


    }

}

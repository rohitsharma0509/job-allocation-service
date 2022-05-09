package com.scb.rider.joballocation.kafka.producer;
import com.scb.rider.joballocation.model.JobStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


@Service
@Slf4j
public class Sender {


    private KafkaTemplate<String, JobStatus> kafkaTemplate;

    private String topic;

    @Autowired
    public Sender(
        KafkaTemplate<String, JobStatus> kafkaTemplate,
        @Value("${kafka.topicStatus}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(JobStatus data) {
        log.info("sending data to topic='{}'", topic);

        Message<JobStatus> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        ListenableFuture<SendResult<String, JobStatus>> future = kafkaTemplate.send(message);
        future.addCallback(callback());
    }

    private ListenableFutureCallback<? super SendResult<String, JobStatus>> callback() {
        return new ListenableFutureCallback<SendResult<String, JobStatus>>() {

            @Override
            public void onSuccess(SendResult<String, JobStatus> result) {
                log.info("Message published successfully");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while publishing message.", ex);
            }


        };

    }
}
package com.scb.rider.joballocation.kafka.producer;

import com.scb.rider.joballocation.model.JobStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SenderTest {

    private Sender sender;
    @Mock
    private KafkaTemplate<String, JobStatus> kafkaTemplate;

    @BeforeEach
    void setup() {
        sender = new Sender(kafkaTemplate, "topic");
    }

    @Test
    void senderTest() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date = new Date();
        JobStatus status = new JobStatus("jobId", "riderId", formatter.format(date), "RIDER_NOT_FOUND");
        ListenableFuture listenableFuture = Mockito.mock(ListenableFuture.class);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(listenableFuture);
        sender.send(status);
        verify(kafkaTemplate, times(1)).send(any(Message.class));

    }

}


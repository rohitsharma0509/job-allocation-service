package com.scb.rider.joballocation.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.algorithm.JobAllocationAlgorithm;
import com.scb.rider.joballocation.model.JobDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListenerTest {

    @InjectMocks
    private Listener listener;

    @Mock
    private JobAllocationAlgorithm jobAllocationAlgorithm;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        listener = new Listener(objectMapper, jobAllocationAlgorithm);

    }

    @Test
    void testReceive() throws IOException {
        JobDetail jobDetail = new JobDetail();
        doNothing().when(jobAllocationAlgorithm).allocate(any(JobDetail.class));
        listener.receive(objectMapper.writeValueAsString(jobDetail), null);
        verify(jobAllocationAlgorithm, times(1)).allocate(any(JobDetail.class));

    }

}

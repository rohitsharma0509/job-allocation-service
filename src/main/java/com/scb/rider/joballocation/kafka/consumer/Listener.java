package com.scb.rider.joballocation.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.algorithm.JobAllocationAlgorithm;
import com.scb.rider.joballocation.model.JobDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class Listener {

  private ObjectMapper objectMapper;

  private JobAllocationAlgorithm jobAllocationAlgorithm;

  @Autowired
  public Listener(ObjectMapper objectMapper, JobAllocationAlgorithm jobAllocationAlgorithm) {
    this.objectMapper = objectMapper;
    this.jobAllocationAlgorithm = jobAllocationAlgorithm;
  }

  @KafkaListener(topics = "${kafka.topic}")
  public void receive(@Payload String data, @Headers MessageHeaders headers) throws IOException {
    log.info("message received: " + data);

    JobDetail jobDetail = objectMapper.readValue(data,JobDetail.class);
    try {
      jobAllocationAlgorithm.allocate(jobDetail);
    }
    catch(Exception ex)
    {
      log.error("Job Allocation Exception:" + ex.getMessage());
    }

  }

}



package com.scb.rider.joballocation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.JobBroadCast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobBroadcastServiceProxyTest {

    private JobBroadcastServiceProxy jobBroadcastServiceProxy;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {

        jobBroadcastServiceProxy = new JobBroadcastServiceProxy(restTemplate, objectMapper, "http://jobservice/path");
    }

    @Test
    void testFindRiders() {
        JobBroadCast jobBroadCast = new JobBroadCast();
        when(restTemplate.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(ResponseEntity.ok("success"));
        String[] riders = new String[1];
        String ret = jobBroadcastServiceProxy.broadCastJob(jobBroadCast);
        assertEquals("success", ret);

    }

    @Test
    void testFindRidersException() {
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .postForEntity(anyString(), any(), any(Class.class));
        JobBroadCast jobBroadCast = new JobBroadCast();
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> jobBroadcastServiceProxy.broadCastJob(jobBroadCast));

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }
}


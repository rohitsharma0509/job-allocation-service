
package com.scb.rider.joballocation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.config.Constants;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.ConfigData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationsServiceProxyTest {

    private OperationsServiceProxy operationsServiceProxy;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {

        operationsServiceProxy = new OperationsServiceProxy(restTemplate, objectMapper, "http://operations-service/path");
    }

    @Test
    void testGetConfigData() {
        ConfigData configData = new ConfigData("1", "key", "value", "ops");
        List<ConfigData> configDataList = new ArrayList<>();
        configDataList.add(configData);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        when(restTemplate.exchange("http://operations-service/path/ops/config/list/1", HttpMethod.GET,
                new HttpEntity<>(headers), new ParameterizedTypeReference<List<ConfigData>>() {
        }))
                .thenReturn(ResponseEntity.ok(configDataList));

        List<ConfigData> configDataList1 = operationsServiceProxy.getConfigDataList("1");
        assertEquals(1, configDataList1.size());
    }

    @Test
    void testGetConfigDataException() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .exchange("http://operations-service/path/ops/config/list/1", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ConfigData>>() {
                });
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> operationsServiceProxy.getConfigDataList("1"));

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }

    @Test
    void testGetRequiredTrainings() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        when(restTemplate.exchange("http://operations-service/path/ops/trainings/required", HttpMethod.GET,
                new HttpEntity<>(headers), new ParameterizedTypeReference<List<String>>() {
                }))
                .thenReturn(ResponseEntity.ok(Arrays.asList(Constants.FOOD)));

        List<String> result = operationsServiceProxy.getAllRequiredTrainings();
        assertEquals(1, result.size());
    }

    @Test
    void testGetRequiredTrainingsException() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .exchange("http://operations-service/path/ops/trainings/required", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<String>>() {
                });
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> operationsServiceProxy.getAllRequiredTrainings());
        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());
    }
}


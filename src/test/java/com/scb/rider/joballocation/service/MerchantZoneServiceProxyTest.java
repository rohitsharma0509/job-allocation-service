package com.scb.rider.joballocation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.ZoneEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
class MerchantZoneServiceProxyTest {
    private LocationServiceProxy locationServiceProxy;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {

        locationServiceProxy = new LocationServiceProxy(restTemplate, objectMapper, "http://jobservice/path");
    }

    @Test
    void testMerchantZoneProxy() {
        ZoneEntity zoneEntity = new ZoneEntity(1, 1, "zoneName", 5, 5, 25);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(ResponseEntity.ok(zoneEntity));
        ZoneEntity zoneEntity1 = locationServiceProxy.findZone("1.1", "1.2");

        assertEquals(zoneEntity1.getZoneName(), zoneEntity.getZoneName());

    }

    @Test
    void testException() {

        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
        String[] riders = new String[1];
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> locationServiceProxy.findZone("1.1", "1.2"));

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }
}

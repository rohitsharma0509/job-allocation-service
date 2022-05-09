package com.scb.rider.joballocation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.RiderProfile;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailableRiderServiceProxyTest {
    private AvailableRiderServiceProxy availableRiderServiceProxy;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {

        availableRiderServiceProxy = new AvailableRiderServiceProxy(restTemplate, objectMapper, "http://jobservice/path");
    }

    @Test
    void testMerchantZoneProxy() {
        RiderProfile riderProfile1 = RiderProfile.builder().id("rider-1").build();
        RiderProfile riderProfile2 = RiderProfile.builder().id("rider-2").build();
        List<RiderProfile> riderListActual = new ArrayList<>();
        riderListActual.add(riderProfile1);
        riderListActual.add(riderProfile2);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        when(restTemplate.exchange("http://jobservice/path/scoring-params?status=Active&riderStatus=AUTHORIZED", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<RiderProfile>>() {
        }))
                .thenReturn(ResponseEntity.ok(riderListActual));
        List<RiderProfile> riderList = availableRiderServiceProxy.findRiders();
        assertEquals(2, riderList.size());

    }

    @Test
    void testException() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .exchange("http://jobservice/path/scoring-params?status=Active&riderStatus=AUTHORIZED", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<RiderProfile>>() {
                });
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> availableRiderServiceProxy.findRiders());

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }
}

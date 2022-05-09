
package com.scb.rider.joballocation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.LocationRiderList;
import com.scb.rider.joballocation.model.Rider;
import com.scb.rider.joballocation.model.ZoneEntity;
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
class LocationServiceProxyTest {

    private LocationServiceProxy locationServiceProxy;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {

        locationServiceProxy = new LocationServiceProxy(restTemplate, objectMapper, "http://jobservice/path");
    }

    @Test
    void testFindRiders() {
        Rider rider = Rider.builder().riderId("id").lat(55.1).lon(55.1).distance(8531.87).build();

        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(ResponseEntity.ok(new Rider[]{rider}));
        String[] riders = new String[1];
        LocationRiderList riderRequest = LocationRiderList.builder().riderList(riders).build();
        Rider[] riderList = locationServiceProxy.findRiders("1.1", "1.2", 5);
        assertEquals(1, riderList.length);

    }

    @Test
    void testFindRidersByBoxType() {
        Rider rider = Rider.builder().riderId("id").lat(55.1).lon(55.1).distance(8531.87).build();

        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(ResponseEntity.ok(new Rider[]{rider}));
        String[] riders = new String[1];
        LocationRiderList riderRequest = LocationRiderList.builder().riderList(riders).build();
        Rider[] riderList = locationServiceProxy.findRidersByBoxType("1.1", "1.2", "LARGE",Boolean.FALSE, 5);
        assertEquals(1, riderList.length);

    }

    @Test
    void testFindRidersByExpress() {
        Rider rider = Rider.builder().riderId("id").lat(55.1).lon(55.1).distance(8531.87).build();

        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(ResponseEntity.ok(new Rider[]{rider}));
        String[] riders = new String[1];
        LocationRiderList riderRequest = LocationRiderList.builder().riderList(riders).build();
        Rider[] riderList = locationServiceProxy.findExpressRider("1.1", "1.2",5);
        assertEquals(1, riderList.length);

    }

    @Test
    void testFindZone() {
        ZoneEntity zone = new ZoneEntity(1, 50100, "zone-1", 10, 10, 3000);

        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class)))
                .thenReturn(ResponseEntity.ok(zone));
        ZoneEntity zoneResponse = locationServiceProxy.findZone("1.1", "1.2");
        assertEquals(1, zoneResponse.getZoneId());

    }

    @Test
    void testFindRidersByPointX() {
        Rider rider = Rider.builder().riderId("id").lat(55.1).lon(55.1).distance(8531.87).build();

        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(ResponseEntity.ok(new Rider[]{rider}));
        String[] riders = new String[1];
        LocationRiderList riderRequest = LocationRiderList.builder().riderList(riders).build();
        Rider[] riderList = locationServiceProxy.findPointxRider("1.1", "1.2",5);
        assertEquals(1, riderList.length);

    }

    @Test
    void testFindRidersException() {
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .getForEntity(anyString(), any(Class.class));
        String[] riders = new String[1];
        LocationRiderList riderRequest = LocationRiderList.builder().riderList(riders).build();
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> locationServiceProxy.findRiders("1.1", "1.2", 5));

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }

    @Test
    void testFindRidersByBoxTypeException() {
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .getForEntity(anyString(), any(Class.class));
        String[] riders = new String[1];
        LocationRiderList riderRequest = LocationRiderList.builder().riderList(riders).build();
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> locationServiceProxy.findRidersByBoxType("1.1", "1.2", "LARGE", Boolean.FALSE, 5));

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }

    @Test
    void testFindRidersByExpressException() {
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .getForEntity(anyString(), any(Class.class));
        String[] riders = new String[1];
        LocationRiderList riderRequest = LocationRiderList.builder().riderList(riders).build();
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> locationServiceProxy.findExpressRider("1.1", "1.2",5));

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }

    @Test
    void testFindZoneException() {
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .exchange(anyString(), any(), any(), any(Class.class));
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> locationServiceProxy.findZone("1.1", "1.2"));

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }

    @Test
    void testFindRidersByPointXException() {
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .getForEntity(anyString(), any(Class.class));
        String[] riders = new String[1];
        LocationRiderList riderRequest = LocationRiderList.builder().riderList(riders).build();
        JobAllocationException ex = assertThrows(JobAllocationException.class, () -> locationServiceProxy.findPointxRider("1.1", "1.2",5));

        assertEquals("404", ex.getErrorCode());
        assertEquals("Failure", ex.getErrorMessage());

    }
}


package com.scb.rider.joballocation.controller;

import com.scb.rider.joballocation.algorithm.JobAllocationAlgorithm;
import com.scb.rider.joballocation.model.ConfigData;
import com.scb.rider.joballocation.model.JobDetail;
import com.scb.rider.joballocation.model.Rider;
import com.scb.rider.joballocation.model.RiderBroadCast;
import com.scb.rider.joballocation.service.OperationsServiceProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobAllocationControllerTest {

    @Mock
    private JobAllocationAlgorithm jobAllocationAlgorithm;

    private JobAllocationController jobAllocationController;

    @Mock
    private OperationsServiceProxy operationsServiceProxy;

    @BeforeEach
    void setup() {

        jobAllocationController = new JobAllocationController(jobAllocationAlgorithm, operationsServiceProxy);
    }

    @Test
    void getRidersTest() {
        when(jobAllocationAlgorithm.findNearestRiders(any(JobDetail.class), anyInt())).thenReturn(getRiderList(5));
        JobDetail jobDetail = new JobDetail();
        jobDetail.setJobDetail("job-detail");
        jobDetail.setJobId("job-id");
        when(operationsServiceProxy.getConfigDataList(Mockito.anyString())).thenReturn(getConfigData());
        ResponseEntity<List<RiderBroadCast>> responseEntity = jobAllocationController.getRiders(jobDetail, 5);
        List<RiderBroadCast> riderBroadCastList = responseEntity.getBody();
        for (RiderBroadCast r: riderBroadCastList) {
            assertEquals(1.5, r.getDistance());
        }
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(5, Objects.requireNonNull(responseEntity.getBody()).size());
    }

    @Test
    void getRidersTestWithException() {
        when(jobAllocationAlgorithm.findNearestRiders(any(JobDetail.class), anyInt())).thenReturn(getRiderList(5));
        JobDetail jobDetail = new JobDetail();
        jobDetail.setJobDetail("job-detail");
        jobDetail.setJobId("job-id");
        when(operationsServiceProxy.getConfigDataList(Mockito.anyString())).thenThrow(new RuntimeException());
        ResponseEntity<List<RiderBroadCast>> responseEntity = jobAllocationController.getRiders(jobDetail, 5);
        List<RiderBroadCast> riderBroadCastList = responseEntity.getBody();
        for (RiderBroadCast r: riderBroadCastList) {
            assertEquals(1.0, r.getDistance());
        }
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(5, Objects.requireNonNull(responseEntity.getBody()).size());
    }

    private Rider[] getRiderList(int n) {
        Rider[] riderList = new Rider[n];

        for (int i = 0; i < n; i++) {
            riderList[i] = new Rider("rider-" + i, 13.2, 100.22, 1000.0, 100.0, false, false, "1");
        }

        return riderList;
    }

    private List<ConfigData> getConfigData() {
        List<ConfigData> configDataList = new ArrayList<>();
        ConfigData configData = new ConfigData("id","variationFactor","50","OPS");
        configDataList.add(configData);
        return configDataList;
    }

}

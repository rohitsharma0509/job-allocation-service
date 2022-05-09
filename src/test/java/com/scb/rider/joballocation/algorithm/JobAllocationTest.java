package com.scb.rider.joballocation.algorithm;

import com.scb.rider.joballocation.config.Constants;
import com.scb.rider.joballocation.entity.JobRidersEntity;
import com.scb.rider.joballocation.model.*;
import com.scb.rider.joballocation.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JobAllocationTest {


    @Mock
    LocationServiceProxy locationServiceProxy;
    @Mock
    JobBroadcastServiceProxy jobBroadcastServiceProxy;
    @Mock
    AvailableRiderServiceProxy availableRiderServiceProxy;
    @Mock
    JobRidersCacheService jobRidersCacheService;
    @Mock
    OperationsServiceProxy operationsServiceProxy;
    private JobAllocationAlgorithm jobAllocationAlgorithm;
    @Mock
    private JobDetail jobRequest;
    private ZoneEntity zoneEntity;
    private Rider[] riderResponse;
    private JobRidersEntity jobRidersEntity;

    static JobDetail createResponse() {
        Location[] locationsArray = new Location[]{
                new Location(
                        "address-name",
                        "address",
                        "13.02",
                        "100.23",
                        "contact-name",
                        "contact-phone",
                        1
                )
        };
        JobDetail jobDetail = new JobDetail();
        jobDetail.setJobId("1");
        jobDetail.setLocationList(locationsArray);
        return jobDetail;
    }

    static ZoneEntity createZoneEntity() {
        return new ZoneEntity(1, 1, "zoneName", 5, 5, 25);

    }

    static ZoneEntity zoneEntityWithMaxRidersJobAsOne() {
        return new ZoneEntity(1, 1, "zoneName", 1, 1, 25);

    }

    static Optional<JobRidersEntity> createJobRidersEntity() {
        return Optional.of(JobRidersEntity.builder().jobId("1").riderList(createRiderResponseList()).build());

    }

    static ZoneEntity zoneEntityWithoutMaxDistanceToBroadcast() {
        return new ZoneEntity(1, 1, "zoneName", 5, 5, null);
    }

    static ZoneEntity zoneEntityWithMaxDistanceToBroadcastZero() {
        return new ZoneEntity(1, 1, "zoneName", 5, 5, 0);
    }

    static Rider[] createRiderResponseList() {
        Rider rider1 =new Rider("1", 1.1, 1.1, 8531.87, 8531.87, false, false, "1");
        Rider rider2 = new Rider("2", 1.1, 1.1, 8531.87, 8531.87, false, false, "1");
        Rider[] riderList = new Rider[2];
        riderList[0] = rider1;
        riderList[1] = rider2;
        return riderList;
    }

    static List<RiderProfile> createRiderList() {
        RiderProfile riderProfile = RiderProfile.builder().id("rider-1").evBikeUser(true).preferredZone("10").rentingToday(true).build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile);
        return riderProfileList;
    }


    @BeforeEach
    void initTest() {
        jobRequest = createResponse();
        jobAllocationAlgorithm = new JobAllocationAlgorithm(locationServiceProxy, availableRiderServiceProxy, jobBroadcastServiceProxy, jobRidersCacheService, operationsServiceProxy);
        zoneEntity = createZoneEntity();
        riderResponse = createRiderResponseList();

    }

    @Test
    void testZoneNotFoundException() {

        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(locationServiceProxy)
                .findZone(anyString(), anyString());
        assertThrows(HttpClientErrorException.class, () -> jobAllocationAlgorithm.allocate(jobRequest));


    }

    @Test
    void findNearestRiderException() {
        when(locationServiceProxy.findZone(anyString(), anyString()))
                .thenReturn(zoneEntity);
        RiderProfile riderProfile = RiderProfile.builder().id("rider-1").build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile);

        when(availableRiderServiceProxy.findRiders())
                .thenReturn(riderProfileList);
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(locationServiceProxy)
                .findRiders(anyString(), anyString(), any());

        assertThrows(HttpClientErrorException.class, () -> jobAllocationAlgorithm.allocate(jobRequest));


    }

    @Test
    void jobBroadCastException() {
        when(locationServiceProxy.findZone(anyString(), anyString()))
                .thenReturn(zoneEntity);
        RiderProfile riderProfile = RiderProfile.builder().id("rider-1").build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile);

        when(availableRiderServiceProxy.findRiders())
                .thenReturn(riderProfileList);
        when(locationServiceProxy.findRiders(anyString(), anyString(), any())).thenReturn(riderResponse);
        when(operationsServiceProxy.getConfigDataList(Mockito.anyString())).thenReturn(getConfigData());
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(jobBroadcastServiceProxy)
                .broadCastJob(any(JobBroadCast.class));
        assertThrows(HttpClientErrorException.class, () -> jobAllocationAlgorithm.allocate(jobRequest));


    }

    @Test
    void JobAllocationTestCheck() {
        when(locationServiceProxy.findZone(anyString(), anyString()))
                .thenReturn(zoneEntity);
        RiderProfile riderProfile = RiderProfile.builder().id("rider-1").build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile);

        when(operationsServiceProxy.getConfigDataList(Mockito.anyString())).thenReturn(getConfigData());
        when(operationsServiceProxy.getAllRequiredTrainings()).thenReturn(Arrays.asList(Constants.FOOD));
        when(availableRiderServiceProxy.findRiders())
                .thenReturn(riderProfileList);
        when(locationServiceProxy.findRiders(anyString(), anyString(), any())).thenReturn(riderResponse);
        when(jobBroadcastServiceProxy.broadCastJob(any(JobBroadCast.class))).thenReturn("success");

        jobAllocationAlgorithm.allocate(jobRequest);
        assertNotNull(riderProfileList);

    }

    @Test
    void JobAllocationTestCheckByBoxType() {
        when(locationServiceProxy.findZone(anyString(), anyString()))
                .thenReturn(zoneEntity);
        RiderProfile riderProfile = RiderProfile.builder().id("rider-1").build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile);

        when(operationsServiceProxy.getConfigDataList(Mockito.anyString())).thenReturn(getConfigData());
        when(operationsServiceProxy.getAllRequiredTrainings()).thenReturn(Arrays.asList(Constants.MART));
        when(availableRiderServiceProxy.findRiders())
                .thenReturn(riderProfileList);
        when(locationServiceProxy.findRidersByBoxType(anyString(), anyString(), anyString(), anyBoolean(), any())).thenReturn(riderResponse);
        when(jobBroadcastServiceProxy.broadCastJob(any(JobBroadCast.class))).thenReturn("success");

        JobDetail jobRequestnNew = jobRequest;
        jobRequestnNew.setJobType("2");
        jobAllocationAlgorithm.allocate(jobRequestnNew);
        assertNotNull(riderProfileList);

    }
    @Test
    void JobAllocationTestCheckByMartJobs() {
        when(locationServiceProxy.findZone(anyString(), anyString()))
                .thenReturn(zoneEntity);
        RiderProfile riderProfile = RiderProfile.builder().id("rider-1").build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile);

        when(operationsServiceProxy.getConfigDataList(Mockito.anyString())).thenReturn(getConfigData());
        when(operationsServiceProxy.getAllRequiredTrainings()).thenReturn(Arrays.asList(Constants.EXPRESS));
        when(availableRiderServiceProxy.findRiders())
                .thenReturn(riderProfileList);
        when(locationServiceProxy.findExpressRider(anyString(),any(),anyInt())).thenReturn(riderResponse);
        when(jobBroadcastServiceProxy.broadCastJob(any(JobBroadCast.class))).thenReturn("success");

        JobDetail jobRequestnNew = jobRequest;
        jobRequestnNew.setJobType("1");
        jobAllocationAlgorithm.allocate(jobRequestnNew);
        assertNotNull(riderProfileList);

    }

    @Test
    void JobAllocationTestCheckByPointXJobs() {
        when(locationServiceProxy.findZone(anyString(), anyString()))
                .thenReturn(zoneEntity);
        RiderProfile riderProfile = RiderProfile.builder().id("rider-1").build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile);

        when(operationsServiceProxy.getConfigDataList(Mockito.anyString())).thenReturn(getConfigData());
        when(operationsServiceProxy.getAllRequiredTrainings()).thenReturn(Arrays.asList(Constants.POINTX));
        when(availableRiderServiceProxy.findRiders())
                .thenReturn(riderProfileList);
        when(locationServiceProxy.findPointxRider(anyString(),any(),anyInt())).thenReturn(riderResponse);
        when(jobBroadcastServiceProxy.broadCastJob(any(JobBroadCast.class))).thenReturn("success");

        JobDetail jobRequestnNew = jobRequest;
        jobRequestnNew.setJobType("4");
        jobAllocationAlgorithm.allocate(jobRequestnNew);
        assertNotNull(riderProfileList);

    }

    @Test
    void JobAllocationTestCheck_SaveCache() {
        when(locationServiceProxy.findZone(anyString(), anyString()))
                .thenReturn(zoneEntityWithMaxRidersJobAsOne());
        RiderProfile riderProfile = RiderProfile.builder().id("rider-1").build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile);

        when(operationsServiceProxy.getConfigDataList(Mockito.anyString())).thenReturn(getConfigData());
        when(availableRiderServiceProxy.findRiders())
                .thenReturn(riderProfileList);
        when(locationServiceProxy.findRiders(anyString(), anyString(), any())).thenReturn(riderResponse);
        when(jobBroadcastServiceProxy.broadCastJob(any(JobBroadCast.class))).thenReturn("success");


        jobAllocationAlgorithm.allocate(jobRequest);
        assertNotNull(riderProfileList);

    }

    @Test
    void findNearestRidersTest() {
        JobDetail jobDetail = createResponse();
        when(locationServiceProxy.findZone(anyString(), anyString())).thenReturn(createZoneEntity());
        when(availableRiderServiceProxy.findRiders()).thenReturn(createRiderList());
        when(locationServiceProxy.findRiders(anyString(), anyString(), anyInt()))
                .thenReturn(createRiderResponseList());

        Rider[] riders = jobAllocationAlgorithm.findNearestRiders(jobDetail, 2);
        assertEquals(2, riders.length);
        verify(locationServiceProxy, times(1))
                .findZone(anyString(), anyString());
        verify(locationServiceProxy, times(1))
                .findRiders(anyString(), anyString(), anyInt());

    }

    @Test
    void findNearestRidersWithMaxRidersLessThanAvailableRidersTest() {
        JobDetail jobDetail = createResponse();
        when(locationServiceProxy.findZone(anyString(), anyString())).thenReturn(zoneEntityWithMaxRidersJobAsOne());
        when(availableRiderServiceProxy.findRiders()).thenReturn(createRiderList());
        when(locationServiceProxy.findRiders(anyString(), anyString(), anyInt()))
                .thenReturn(createRiderResponseList());

        Rider[] riders = jobAllocationAlgorithm.findNearestRiders(jobDetail, 2);
        assertEquals(2, riders.length);
        verify(locationServiceProxy, times(1))
                .findZone(anyString(), anyString());
        verify(locationServiceProxy, times(1))
                .findRiders(anyString(), anyString(), anyInt());

    }

    @Test
    void findNearestRidersGetRidersFromCacheTest() {
        JobDetail jobDetail = createResponse();
        when(locationServiceProxy.findZone(anyString(), anyString())).thenReturn(zoneEntityWithMaxRidersJobAsOne());
        when(locationServiceProxy.findRiders(anyString(), anyString(), anyInt()))
                .thenReturn(createRiderResponseList());
        when(jobRidersCacheService.getRidersByJobId(anyString())).thenReturn(createJobRidersEntity());

        Rider[] riders = jobAllocationAlgorithm.findNearestRiders(jobDetail, 2);
        assertEquals(2, riders.length);
        verify(locationServiceProxy, times(1))
                .findZone(anyString(), anyString());
        verify(availableRiderServiceProxy, never())
                .findRiders();
        verify(locationServiceProxy, never())
                .findRiders(anyString(), anyString(), anyInt());

    }

    @Test
    void findNearestRidersTestWithoutMaxBroadcastDistance() {
        JobDetail jobDetail = createResponse();
        when(locationServiceProxy.findZone(anyString(), anyString())).thenReturn(zoneEntityWithoutMaxDistanceToBroadcast());
        when(availableRiderServiceProxy.findRiders()).thenReturn(createRiderList());
        when(locationServiceProxy.findRiders(anyString(), anyString(), anyInt()))
                .thenReturn(createRiderResponseList());

        Rider[] riders = jobAllocationAlgorithm.findNearestRiders(jobDetail, 2);
        assertEquals(2, riders.length);
        verify(locationServiceProxy, times(1))
                .findZone(anyString(), anyString());
        verify(locationServiceProxy, times(1))
                .findRiders(anyString(), anyString(), anyInt());

    }

    @Test
    void findNearestRidersTestWitMaxBroadcastDistanceZero() {
        JobDetail jobDetail = createResponse();
        when(locationServiceProxy.findZone(anyString(), anyString())).thenReturn(zoneEntityWithMaxDistanceToBroadcastZero());
        when(availableRiderServiceProxy.findRiders()).thenReturn(createRiderList());
        when(locationServiceProxy.findRiders(anyString(), anyString(), anyInt()))
                .thenReturn(createRiderResponseList());

        Rider[] riders = jobAllocationAlgorithm.findNearestRiders(jobDetail, 2);
        assertEquals(2, riders.length);
        verify(locationServiceProxy, times(1))
                .findZone(anyString(), anyString());
        verify(locationServiceProxy, times(1))
                .findRiders(anyString(), anyString(), anyInt());

    }

    @Test
    void findNearestRidersWithPreferenceTest() {
        JobDetail jobDetail = createResponse();
        RiderProfile riderProfile1 = RiderProfile.builder().id("1").evBikeUser(true).preferredZone("1").rentingToday(true).build();
        RiderProfile riderProfile2 = RiderProfile.builder().id("2").evBikeUser(true).preferredZone("10").rentingToday(true).build();
        List<RiderProfile> riderProfileList = new ArrayList<>();
        riderProfileList.add(riderProfile1);
        riderProfileList.add(riderProfile2);
        when(locationServiceProxy.findZone(anyString(), anyString())).thenReturn(createZoneEntity());
        when(availableRiderServiceProxy.findRiders()).thenReturn(riderProfileList);
        when(locationServiceProxy.findRiders(anyString(), anyString(), anyInt()))
                .thenReturn(createRiderResponseList());
        ConfigData applyPreferredZonePreference = new ConfigData("1", "applyPreferredZonePreference", "true", "OPS");
        ConfigData applyEvPreference = new ConfigData("2", "applyEvPreference", "true", "OPS");
        ConfigData evPreferenceWeightage = new ConfigData("3", "evPreferenceWeightage", "250", "OPS");
        ConfigData preferredZonePreferenceWeightage = new ConfigData("4", "preferredZonePreferenceWeightage", "500", "OPS");

        List<ConfigData> configDataList = new ArrayList<>();
        configDataList.add(applyPreferredZonePreference);
        configDataList.add(applyEvPreference);
        configDataList.add(evPreferenceWeightage);
        configDataList.add(preferredZonePreferenceWeightage);

        when(operationsServiceProxy.getConfigDataList(anyString())).thenReturn(configDataList);

        Rider[] riders = jobAllocationAlgorithm.findNearestRiders(jobDetail, 2);
        assertEquals(2, riders.length);
        verify(locationServiceProxy, times(1))
                .findZone(anyString(), anyString());
        verify(locationServiceProxy, times(1))
                .findRiders(anyString(), anyString(), anyInt());

    }


    private List<ConfigData> getConfigData() {
        List<ConfigData> configDataList = new ArrayList<>();
        ConfigData configData = new ConfigData("id","variationFactor","50","OPS");
        configDataList.add(configData);
        return configDataList;
    }
}

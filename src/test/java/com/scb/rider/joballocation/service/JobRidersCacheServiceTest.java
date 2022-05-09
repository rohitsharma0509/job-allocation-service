package com.scb.rider.joballocation.service;

import com.scb.rider.joballocation.entity.JobRidersEntity;
import com.scb.rider.joballocation.model.Rider;
import com.scb.rider.joballocation.repository.JobRidersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobRidersCacheServiceTest {

    @Mock
    private JobRidersRepository jobRidersRepository;

    @InjectMocks
    @Autowired
    private JobRidersCacheService jobRidersCacheService;

    static Optional<JobRidersEntity> createJobRidersEntity() {
        return Optional.of(JobRidersEntity.builder().jobId("1").riderList(createRiderResponseList()).build());

    }

    static Rider[] createRiderResponseList() {
        Rider rider1 = new Rider("1", 1.1, 1.1, 8531.87, 8531.87, false, false, "1");
        Rider rider2 = new Rider("2", 1.1, 1.1, 8531.87, 8531.87,false, false, "1");
        Rider[] riderList = new Rider[2];
        riderList[0] = rider1;
        riderList[1] = rider2;
        return riderList;
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jobRidersCacheService, "ttl", "180");
    }

    @Test
    void getRidersByJobIdTest() {
        when(jobRidersRepository.findById(anyString())).thenReturn(createJobRidersEntity());
        Optional<JobRidersEntity> riderList = jobRidersCacheService.getRidersByJobId("1");

        assertNotNull(riderList);
        assertEquals(2, riderList.get().getRiderList().length);
    }

    @Test
    void saveRidersTest() {

        when(jobRidersRepository.save(any())).thenReturn(any());
        jobRidersCacheService.save("1", createRiderResponseList());

    }

    @Test
    void saveRidersListEmptyTest() {

        Rider[] riders = new Rider[0];
        jobRidersCacheService.save("1", riders);
        verify(jobRidersRepository, never())
                .save(any());

    }

    @Test
    void saveRidersExceptionTest() {
        ReflectionTestUtils.setField(jobRidersCacheService, "ttl", "180-test");
        NumberFormatException ex = assertThrows(NumberFormatException.class, () -> jobRidersCacheService.save("1", createRiderResponseList()));
    }
}

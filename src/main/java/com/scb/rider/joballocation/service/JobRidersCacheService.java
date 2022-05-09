package com.scb.rider.joballocation.service;

import com.scb.rider.joballocation.entity.JobRidersEntity;
import com.scb.rider.joballocation.model.Rider;
import com.scb.rider.joballocation.repository.JobRidersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Configuration
@Service
@Slf4j
public class JobRidersCacheService {

    @Value("${redis.ttl}")
    private String ttl;

    @Autowired
    private JobRidersRepository jobRidersRepository;

    public Optional<JobRidersEntity> getRidersByJobId(String jobId) {
        return jobRidersRepository.findById(jobId);
    }

    public void save(String jobId, Rider[] riderList) {

        log.info("Saving jobid-Riders entities mapping in cache ");
        if (!ObjectUtils.isEmpty(riderList)) {
            log.info("TTL : {} ", ttl);
            jobRidersRepository.save(JobRidersEntity.builder().jobId(jobId).riderList(riderList).ttl(Long.parseLong(ttl)).build());
        }
    }
}

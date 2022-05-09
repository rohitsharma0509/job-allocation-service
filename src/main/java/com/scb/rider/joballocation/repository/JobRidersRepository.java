package com.scb.rider.joballocation.repository;

import com.scb.rider.joballocation.entity.JobRidersEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRidersRepository extends CrudRepository<JobRidersEntity, String> {

    Optional<JobRidersEntity> findById(String jobId);

    JobRidersEntity save(JobRidersEntity jobRidersbEntity);

}

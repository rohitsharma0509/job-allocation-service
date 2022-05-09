package com.scb.rider.joballocation.entity;

import com.scb.rider.joballocation.model.Rider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;


@RedisHash("JobRidersEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class JobRidersEntity {
    @Id
    private String jobId;
    private Rider[] riderList;
    @TimeToLive
    Long ttl;
}
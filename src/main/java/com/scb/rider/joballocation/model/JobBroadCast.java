package com.scb.rider.joballocation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class JobBroadCast {
    private JobDetail jobDetails;
    private List<RiderBroadCast> riders;
    private Integer maxJobsForRider;
    private Integer maxRidersForJob;
}

package com.scb.rider.joballocation.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ZoneEntity {
    private Integer zoneId;
    private Integer postalCode;
    private String zoneName;
    private Integer maxJobsForRider;
    private Integer maxRidersForJob;
    private Integer maxDistanceToBroadcast;
}

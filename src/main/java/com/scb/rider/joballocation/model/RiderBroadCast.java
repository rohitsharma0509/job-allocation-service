package com.scb.rider.joballocation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class RiderBroadCast {
    private String riderId;
    private Integer rank;
    private Double distance;
}

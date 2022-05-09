package com.scb.rider.joballocation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class JobStatus {
    private String jobId;
    private String riderId;
    private String dateTime;
    private String status;
}

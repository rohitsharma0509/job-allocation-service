package com.scb.rider.joballocation.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobDetail {

  private String jobDetail;
  private String jobId;
  private String jobDate;
  private String jobStatus;
  private String JobStatusEn;
  private String jobStatusTh;
  private String jobDesc;
  private String startTime;
  private String finishTime;
  private String haveReturn;
  private String jobType;
  private String option;
  private Float totalDistance;
  private Float totalWeight;
  private Float totalSize;
  private String remark;
  private Integer userType;
  private Float normalPrice;
  private Float netPrice;
  private Float discount;
  private Integer rating;
  private Location[] locationList;
  private String orderId;
  private List<OrderItems> orderItems;
}



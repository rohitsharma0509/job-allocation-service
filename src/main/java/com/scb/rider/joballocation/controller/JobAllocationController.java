package com.scb.rider.joballocation.controller;

import com.scb.rider.joballocation.algorithm.JobAllocationAlgorithm;
import com.scb.rider.joballocation.config.Constants;
import com.scb.rider.joballocation.model.JobDetail;
import com.scb.rider.joballocation.model.Rider;
import com.scb.rider.joballocation.model.RiderBroadCast;
import com.scb.rider.joballocation.service.OperationsServiceProxy;
import com.scb.rider.joballocation.util.CommonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@Validated
@RequestMapping("/find-riders")
public class JobAllocationController {

  private JobAllocationAlgorithm jobAllocationAlgorithm;

  private OperationsServiceProxy operationsServiceProxy;

  @Autowired
  public JobAllocationController(JobAllocationAlgorithm jobAllocationAlgorithm, OperationsServiceProxy operationsServiceProxy)
  {
    this.jobAllocationAlgorithm = jobAllocationAlgorithm;
    this.operationsServiceProxy = operationsServiceProxy;
  }

  @PostMapping("/allocate")
  public ResponseEntity<Void> allocateRiders(@RequestBody JobDetail jobDetail){
    jobAllocationAlgorithm.allocate(jobDetail);
    return ResponseEntity.ok().build();

  }

  @PostMapping
  public ResponseEntity<List<RiderBroadCast>> getRiders(@RequestBody JobDetail jobDetail, @RequestParam("limit") Integer limit){
    log.info("Getting top {} riders for job id = {}", limit, jobDetail.getJobId());
    Rider[] riders = jobAllocationAlgorithm.findNearestRiders(jobDetail,limit);
    List<RiderBroadCast> riderBroadCastList = new ArrayList<>();
    int rank = 1;
    String variationFactor = "0";
    try{
      variationFactor = operationsServiceProxy.getConfigDataList(Constants.VARIATION_FACTOR).get(0).getValue();
    } catch (Exception e){
      log.error("Exception occurred while calling ops-service to get variation factor for job distance ", e);
    }
    for (Rider rider : riders) {
      double distance = rider.getDistanceInKms() + Math.round(rider.getDistanceInKms() * Integer.parseInt(variationFactor))/100.0;
      RiderBroadCast riderBroadCast = new RiderBroadCast(rider.getRiderId(), rank, CommonUtils.round(distance));
      riderBroadCastList.add(riderBroadCast);
      rank++;
    }
    return ResponseEntity.ok(riderBroadCastList);
  }
}

package com.scb.rider.joballocation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Location {

  private String addressName;
  private String address;
  private String lat;
  private String lng;
  private String contactName;
  private String contactPhone;
  private Integer seq;


}

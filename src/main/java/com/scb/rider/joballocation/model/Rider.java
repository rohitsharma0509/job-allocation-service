package com.scb.rider.joballocation.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class Rider {
    private String riderId;
    private Double lat;
    private Double lon;
    private Double distance;
    private Double effectiveDistance;
    private Boolean evBikeUser;
    private Boolean rentingToday;
    private String preferredZone;


    public void setDistance(Double distance) {
        this.distance = distance;
        this.effectiveDistance = distance;
    }

    @JsonIgnore
    public Double getDistanceInKms(){
        return Math.round(distance/1000 * 100.0)/100.0;

    }
}

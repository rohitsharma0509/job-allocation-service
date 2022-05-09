package com.scb.rider.joballocation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiderProfile {
    private String id;
    private Boolean evBikeUser;
    private Boolean rentingToday;
    private String preferredZone;
}

package com.scb.rider.joballocation.algorithm;

import com.scb.rider.joballocation.config.Constants;
import com.scb.rider.joballocation.entity.JobRidersEntity;
import com.scb.rider.joballocation.model.*;
import com.scb.rider.joballocation.service.*;
import com.scb.rider.joballocation.util.CommonUtils;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@Component
@Slf4j
public class JobAllocationAlgorithm {
    private final LocationServiceProxy locationServiceProxy;
    private final AvailableRiderServiceProxy availableRiderServiceProxy;
    private final JobBroadcastServiceProxy jobBroadcastServiceProxy;
    private final JobRidersCacheService jobRidersCacheService;
    private final OperationsServiceProxy operationsServiceProxy;
    private final String FOOD_BOX_TYPE_LARGE = "LARGE";
    private final String FOOD_BOX_TYPE_SMALL = "SMALL";

    @Autowired
    JobAllocationAlgorithm(LocationServiceProxy locationServiceProxy,
                           AvailableRiderServiceProxy availableRiderServiceProxy,
                           JobBroadcastServiceProxy jobBroadcastServiceProxy,
                           JobRidersCacheService jobRidersCacheService,
                           OperationsServiceProxy operationsServiceProxy) {
        this.locationServiceProxy = locationServiceProxy;
        this.availableRiderServiceProxy = availableRiderServiceProxy;
        this.jobBroadcastServiceProxy = jobBroadcastServiceProxy;
        this.jobRidersCacheService = jobRidersCacheService;
        this.operationsServiceProxy = operationsServiceProxy;
    }

    public void allocate(JobDetail jobDetail) {

        Location[] locationsArray = jobDetail.getLocationList();
        ZoneEntity zoneEntity = locationServiceProxy.findZone(locationsArray[0].getLng(), locationsArray[0].getLat());
        Rider[] riderList = findAuthorizedRiders(jobDetail, zoneEntity, locationsArray);

        applyPreferences(riderList, zoneEntity);

        // Saving to cache@@3
        log.info("Saving nearby riders {} to cache for jobId {} from allocate(): ", riderList, jobDetail.getJobId());
        jobRidersCacheService.save(jobDetail.getJobId(), riderList);

        if (riderList != null && zoneEntity.getMaxRidersForJob() <= riderList.length) {
            riderList = Arrays.copyOfRange(riderList, 0, Math.min(riderList.length, zoneEntity.getMaxRidersForJob()));
            log.info("New rider list based on count: {} from allocate(): ", Arrays.asList(riderList));
        }
        broadCastJob(
                filterNearByRiders(riderList, zoneEntity), jobDetail, zoneEntity.getMaxJobsForRider(), zoneEntity.getMaxRidersForJob());
    }

    public Rider[] findNearestRiders(JobDetail jobDetail, Integer size) {

        Location[] locationsArray = jobDetail.getLocationList();
        ZoneEntity zoneEntity = locationServiceProxy.findZone(locationsArray[0].getLng(), locationsArray[0].getLat());

        Optional<JobRidersEntity> jobRidersEntity = jobRidersCacheService.getRidersByJobId(jobDetail.getJobId());

        Rider[] riderList = null;

        if (jobRidersEntity.isPresent() && Objects.nonNull(jobRidersEntity.get().getRiderList())) {

            log.info("Getting nearby riders {} for jobID {} from cache", jobRidersEntity.get().getRiderList(), jobDetail.getJobId());
            riderList = jobRidersEntity.get().getRiderList();

        } else {
            log.info("Getting new set of active riders");

            LocationRiderList locationRiderList = LocationRiderList.builder()
                    .riderList(null)
                    .build();

            riderList = findAuthorizedRiders(jobDetail, zoneEntity, locationsArray);
            applyPreferences(riderList, zoneEntity);
            // Saving to cache
            log.info("Saving nearby riders {} to cache for jobId {} from findNearestRiders() : ", riderList, jobDetail.getJobId());
            jobRidersCacheService.save(jobDetail.getJobId(), riderList);
        }

        if (riderList != null && size <= riderList.length) {
            riderList = Arrays.copyOfRange(riderList, 0, Math.min(riderList.length, size));
            log.info("New rider list based on count: {} from findNearestRiders(): ", Arrays.asList(riderList));
        }

        return filterNearByRiders(riderList, zoneEntity);
    }

    public void broadCastJob(Rider[] riderList, JobDetail jobDetail, Integer maxJobsForRider, Integer maxRidersForJob) {
        List<RiderBroadCast> riderBroadCastList = new ArrayList<>();
        int rank = 1;
        String variationFactor = "0";
        try{
            variationFactor = operationsServiceProxy.getConfigDataList(Constants.VARIATION_FACTOR).get(0).getValue();
        } catch (Exception e){
            log.error("Exception occurred while calling ops-service to get variation factor for job distance ", e);
        }
        for (Rider rider : riderList) {
            double distance = rider.getDistanceInKms() + Math.round(rider.getDistanceInKms() * Integer.parseInt(variationFactor))/100.0;
            RiderBroadCast riderBroadCast = new RiderBroadCast(rider.getRiderId(), rank, CommonUtils.round(distance));
            riderBroadCastList.add(riderBroadCast);
            rank++;
        }
        JobBroadCast jobBroadCast = new JobBroadCast(jobDetail, riderBroadCastList, maxJobsForRider, maxRidersForJob);
        log.info("Broadcasting job {} to {} max riders for job and {} max job for riders",
                jobDetail.getJobId(), maxRidersForJob, maxJobsForRider);
        jobBroadcastServiceProxy.broadCastJob(jobBroadCast);
    }

    private Rider[] findAuthorizedRiders(JobDetail jobDetail, ZoneEntity zoneEntity, Location[] locationsArray) {
        Rider[] riderList;
        boolean isTrainingRequired = isTrainingCheckRequired(jobDetail.getJobType());
        log.info("jobType : {}, isTrainingRequired: {}", jobDetail.getJobType(), isTrainingRequired);
        if (Constants.JOB_TYPE_MART_NUMBER.equals(jobDetail.getJobType())) {
            log.info("MART job :" + jobDetail.getJobType());
            riderList = locationServiceProxy.findRidersByBoxType(
                    locationsArray[0].getLng(),
                    locationsArray[0].getLat(),
                    FOOD_BOX_TYPE_LARGE,
                    isTrainingRequired,
                    zoneEntity.getMaxRidersForJob() * 10);
        }
        else if(Constants.JOB_TYPE_EXPRESS_NUMBER.equals(jobDetail.getJobType()) && isTrainingRequired){
            log.info("EXPRESS job: " + jobDetail.getJobType());
            riderList = locationServiceProxy.findExpressRider(
                    locationsArray[0].getLng(),
                    locationsArray[0].getLat(),
                    zoneEntity.getMaxRidersForJob() * 10);
        }
        else if(Constants.JOB_TYPE_POINTX_NUMBER.equals(jobDetail.getJobType()) && isTrainingRequired){
            log.info("POINTX job: " + jobDetail.getJobType());
            riderList = locationServiceProxy.findPointxRider(
                    locationsArray[0].getLng(),
                    locationsArray[0].getLat(),
                    zoneEntity.getMaxRidersForJob() * 10);
        } else {
            riderList = locationServiceProxy.findRiders(
                    locationsArray[0].getLng(),
                    locationsArray[0].getLat(),
                    zoneEntity.getMaxRidersForJob() * 10); //TODO check if this were 10 only earlier
        }
        return riderList;
    }

    private boolean isTrainingCheckRequired(String jobType) {
        boolean isRequired = false;
        List<String> requiredTrainings = operationsServiceProxy.getAllRequiredTrainings();
        if(Constants.JOB_TYPE_FOOD_NUMBER.equals(jobType)) {
            isRequired = requiredTrainings.stream().anyMatch(trainingType -> Constants.FOOD.equalsIgnoreCase(trainingType));
        } else if(Constants.JOB_TYPE_MART_NUMBER.equals(jobType)) {
            isRequired = requiredTrainings.stream().anyMatch(trainingType -> Constants.MART.equalsIgnoreCase(trainingType));
        } else if(Constants.JOB_TYPE_EXPRESS_NUMBER.equals(jobType)) {
            isRequired = requiredTrainings.stream().anyMatch(trainingType -> Constants.EXPRESS.equalsIgnoreCase(trainingType));
        } else if(Constants.JOB_TYPE_POINTX_NUMBER.equals(jobType)) {
            isRequired = requiredTrainings.stream().anyMatch(trainingType -> Constants.POINTX.equalsIgnoreCase(trainingType));
        }
        return isRequired;
    }

    private Rider[] filterNearByRiders(Rider[] riderList, ZoneEntity zoneEntity) {
        List<Rider> newRiderList = new ArrayList<>();
        if (zoneEntity.getMaxDistanceToBroadcast() != null && zoneEntity.getMaxDistanceToBroadcast() > 0) {
            for (Rider riders : riderList) {
                if (riders.getDistanceInKms() <= zoneEntity.getMaxDistanceToBroadcast()) {
                    newRiderList.add(riders);
                }
            }
            return newRiderList.toArray(new Rider[]{});
        } else {
            return riderList;
        }
    }

    /**
     * Applies the preference weightage on the rider's distance from the job location on the basis of renting today and
     * preferred zone flags
     * @param riderList - List of riders with the distances from job location
     * @param zoneEntity - To get the zone id of the job location
     */
    private void applyPreferences(Rider[] riderList, ZoneEntity zoneEntity) {
        final String APPLY_PREFERRED_ZONE_PREFERENCE = "applyPreferredZonePreference";
        final String APPLY_EV_PREFERENCE = "applyEvPreference";
        final String EV_PREFERENCE_WEIGHTAGE = "evPreferenceWeightage";
        final String PREFERRED_ZONE_PREFERENCE_WEIGHTAGE = "preferredZonePreferenceWeightage";

        // get preference flags and weightage
        List<ConfigData> preferenceValues = operationsServiceProxy.getConfigDataList(APPLY_PREFERRED_ZONE_PREFERENCE + ","
                + APPLY_EV_PREFERENCE + ","
                + EV_PREFERENCE_WEIGHTAGE + ","
                + PREFERRED_ZONE_PREFERENCE_WEIGHTAGE);

        Map<String, String> preferenceMap = new HashMap<>();
        preferenceValues.forEach(preference -> preferenceMap.put(preference.getKey(), preference.getValue()));

        //apply the preference logic
        if(Boolean.parseBoolean(preferenceMap.get(APPLY_PREFERRED_ZONE_PREFERENCE)) || Boolean.parseBoolean(preferenceMap.get(APPLY_EV_PREFERENCE))){

            Double evPreferenceWeightage = Double.parseDouble(preferenceMap.get(EV_PREFERENCE_WEIGHTAGE));
            Double preferredZonePreferenceWeightage = Double.parseDouble(preferenceMap.get(PREFERRED_ZONE_PREFERENCE_WEIGHTAGE));
            Integer jobZone = zoneEntity.getZoneId();

            for(Rider rider: riderList){
                boolean rentingToday = rider.getRentingToday() != null && rider.getRentingToday();
                boolean inPreferredZone = rider.getPreferredZone() != null && jobZone == Integer.parseInt(rider.getPreferredZone());
                if(rentingToday && Boolean.parseBoolean(preferenceMap.get(APPLY_EV_PREFERENCE))){
                    rider.setEffectiveDistance(rider.getEffectiveDistance() - evPreferenceWeightage);
                }
                if(inPreferredZone && Boolean.parseBoolean(preferenceMap.get(APPLY_PREFERRED_ZONE_PREFERENCE))){
                    rider.setEffectiveDistance(rider.getEffectiveDistance() - preferredZonePreferenceWeightage);
                }
            }
            Arrays.sort(riderList, Comparator.comparing(Rider::getEffectiveDistance));
        }
    }
}
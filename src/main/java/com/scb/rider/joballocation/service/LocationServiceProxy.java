package com.scb.rider.joballocation.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.exception.ErrorResponse;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.Rider;
import com.scb.rider.joballocation.model.ZoneEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class LocationServiceProxy {

    private RestTemplate restTemplate;
    private String locationServicePath;
    private ObjectMapper objectMapper;

    @Autowired
    public LocationServiceProxy(RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           @Value("${locationService.path}") String locationServicePath) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.locationServicePath = locationServicePath;
    }

    public Rider[] findRiders(String lng, String lat, Integer limit) {
        log.info("Invoking api:{}", locationServicePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationServicePath+"/api/rider/nearby-available-riders")
                    .queryParam("latitude", lat).queryParam("longitude",lng).queryParam("limit",limit);

            ResponseEntity<Rider[]> responseEntity = restTemplate.getForEntity(
                    builder.toUriString(),
                    Rider[].class);
            log.info("Api invocation successful");
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Api request error; ErrorCode:{} ; Message:{}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            ErrorResponse error = parseErrorResponse(ex.getResponseBodyAsString());
            throw new JobAllocationException(error.getErrorCode(), error.getErrorMessage());
        }
    }

    public Rider[] findRidersByBoxType(String lng, String lat, String foodBoxType, Boolean isMartRider, Integer limit) {
        log.info("Invoking api:{}", locationServicePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationServicePath+"/api/rider/nearby-available-riders-boxtype")
                    .queryParam("latitude", lat).queryParam("longitude",lng).queryParam("foodBoxType", foodBoxType)
                    .queryParam("isMartRider", isMartRider)
                    .queryParam("limit",limit);

            ResponseEntity<Rider[]> responseEntity = restTemplate.getForEntity(
                    builder.toUriString(),
                    Rider[].class);
            log.info("locations service Api invocation successful for nearby riders by boxtype");
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Api request error for fetching nearby riders by boxtype; ErrorCode:{} ; Message:{}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            ErrorResponse error = parseErrorResponse(ex.getResponseBodyAsString());
            throw new JobAllocationException(error.getErrorCode(), error.getErrorMessage());
        }
    }
    public Rider[] findExpressRider(String lng, String lat, Integer limit) {
        log.info("Invoking api:{}", locationServicePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationServicePath+"/api/rider/nearby-available-express-riders")
                    .queryParam("latitude", lat).queryParam("longitude",lng).queryParam("limit",limit);

            ResponseEntity<Rider[]> responseEntity = restTemplate.getForEntity(
                    builder.toUriString(),
                    Rider[].class);
            log.info("Api invocation successful");
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Api request error; ErrorCode:{} ; Message:{}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            ErrorResponse error = parseErrorResponse(ex.getResponseBodyAsString());
            throw new JobAllocationException(error.getErrorCode(), error.getErrorMessage());
        }
    }
    public ZoneEntity findZone(String lng, String lat) {
        String location = lng + "," + lat;
        log.info("Invoking api:{}", locationServicePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationServicePath+"/api/zone")
                    .queryParam("location", location);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<ZoneEntity> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    ZoneEntity.class);
            log.info("Api invocation successful");
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Api request error; ErrorCode:{} ; Message:{}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            ErrorResponse error = parseErrorResponse(ex.getResponseBodyAsString());
            throw new JobAllocationException(error.getErrorCode(), error.getErrorMessage());
        }
    }
    @SneakyThrows
    private ErrorResponse parseErrorResponse(String errorResponse){
        return objectMapper.readValue(errorResponse, ErrorResponse.class);

    }

	public Rider[] findPointxRider(String lng, String lat, int limit) {
		log.info("Invoking api:{}", locationServicePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationServicePath+"/api/rider/nearby-available-pointx-riders")
                    .queryParam("latitude", lat).queryParam("longitude",lng).queryParam("limit",limit);

            ResponseEntity<Rider[]> responseEntity = restTemplate.getForEntity(
                    builder.toUriString(),
                    Rider[].class);
            log.info("Api invocation successful");
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Api request error; ErrorCode:{} ; Message:{}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            ErrorResponse error = parseErrorResponse(ex.getResponseBodyAsString());
            throw new JobAllocationException(error.getErrorCode(), error.getErrorMessage());
        }
	}
}

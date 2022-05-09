package com.scb.rider.joballocation.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.exception.ErrorResponse;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.RiderProfile;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Slf4j
public class AvailableRiderServiceProxy {

    private RestTemplate restTemplate;
    private String availableRiderServicePath;
    private ObjectMapper objectMapper;

    @Autowired
    public AvailableRiderServiceProxy(RestTemplate restTemplate,
                                ObjectMapper objectMapper,
                                @Value("${availableRider.path}") String availableRiderServicePath) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.availableRiderServicePath = availableRiderServicePath;
    }

    public List<RiderProfile> findRiders() {
        log.info("Invoking api:{}", availableRiderServicePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(availableRiderServicePath + "/scoring-params")
                    .queryParam("status","Active")
                    .queryParam("riderStatus", "AUTHORIZED");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<List<RiderProfile>> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<RiderProfile>>() {
                    }
            );
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
}

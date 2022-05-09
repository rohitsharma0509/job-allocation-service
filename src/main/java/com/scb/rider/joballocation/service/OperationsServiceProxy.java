package com.scb.rider.joballocation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.exception.ErrorResponse;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.ConfigData;
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
public class OperationsServiceProxy {
    private final RestTemplate restTemplate;
    private final String operationsServicePath;
    private final ObjectMapper objectMapper;
    private final String opsConfigPath = "/ops/config/list/";
    @Autowired
    public OperationsServiceProxy(RestTemplate restTemplate,
                                ObjectMapper objectMapper,
                                @Value("${operationsService.path}") String operationsServicePath) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.operationsServicePath = operationsServicePath;
    }

    public List<ConfigData> getConfigDataList(String keys){
        log.info("Invoking api:{}", operationsServicePath + opsConfigPath + keys);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(operationsServicePath + opsConfigPath + keys);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<List<ConfigData>> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ConfigData>>() {
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

    public List<String> getAllRequiredTrainings() {
        String apiUrl = operationsServicePath + "/ops/trainings/required";
        log.info("Invoking api:{}", apiUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<String>>() {
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

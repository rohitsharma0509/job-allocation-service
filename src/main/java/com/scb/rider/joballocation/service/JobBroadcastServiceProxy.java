package com.scb.rider.joballocation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.rider.joballocation.exception.ErrorResponse;
import com.scb.rider.joballocation.exception.JobAllocationException;
import com.scb.rider.joballocation.model.JobBroadCast;
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
public class JobBroadcastServiceProxy {
    private RestTemplate restTemplate;
    private String broadcastServicePath;
    private ObjectMapper objectMapper;

    @Autowired
    public JobBroadcastServiceProxy(RestTemplate restTemplate,
                                    ObjectMapper objectMapper,
                                    @Value("${broadcastService.path}") String broadcastServicePath) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.broadcastServicePath = broadcastServicePath;
    }
    public String broadCastJob(JobBroadCast job) {
        log.info("Invoking api:{}", broadcastServicePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(broadcastServicePath);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    builder.toUriString(),
                    job,
                    String.class);
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

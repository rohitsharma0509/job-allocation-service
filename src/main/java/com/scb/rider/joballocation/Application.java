package com.scb.rider.joballocation;

import com.scb.rider.tracing.tracer.EnableBasicTracer;
import com.scb.rider.tracing.tracer.logrequest.EnableRequestLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRequestLog
@EnableBasicTracer
public class Application{
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}

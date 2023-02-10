package de.kirill.camunda8springexample;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableZeebeClient
@SpringBootApplication
@EnableScheduling
@Deployment(resources = "classpath*:*.bpmn")
public class Camunda8SpringExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Camunda8SpringExampleApplication.class, args);
    }

}

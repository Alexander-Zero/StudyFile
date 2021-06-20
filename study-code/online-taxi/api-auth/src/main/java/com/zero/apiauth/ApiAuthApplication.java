package com.zero.apiauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@RestController
public class ApiAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiAuthApplication.class, args);
    }


    @Value("${server.port}")
    private int port;

    @GetMapping("/hell0")
    public String hello() {
        return "my port is " + port;
    }

}

package com.example.gataway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GatawayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatawayApplication.class, args);
	}

}

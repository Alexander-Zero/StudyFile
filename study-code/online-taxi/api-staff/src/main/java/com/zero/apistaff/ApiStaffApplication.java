package com.zero.apistaff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ApiStaffApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiStaffApplication.class, args);
	}

}

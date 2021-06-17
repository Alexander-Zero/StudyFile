package com.zero.apiauth;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.transport.decorator.RetryableEurekaHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class ApiAuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiAuthApplication.class, args);
	}
}

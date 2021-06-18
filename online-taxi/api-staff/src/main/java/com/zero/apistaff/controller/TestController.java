package com.zero.apistaff.controller;

import com.sun.jersey.core.util.StringIgnoreCaseKeyComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/6/18
 */
@RestController
public class TestController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test1")
    public String sayHello() {
        String url = "http://api-auth/hell0";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        String body = null;
        if (forEntity.getStatusCode().equals(HttpStatus.OK)) {
            body = forEntity.getBody();
        }
        return "api-user call => " + body;
    }

    @GetMapping("/test")
    public String sayTest() {
        return "test";
    }

}

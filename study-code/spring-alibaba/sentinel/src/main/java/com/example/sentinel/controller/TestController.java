package com.example.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("/test")
    @SentinelResource(value = "test", blockHandler = "testBack")
    public String test() {
        return "test";
    }

    public String testBack(BlockException e) {
        return "xxoo";
    }


    @GetMapping("/get1")
    public String get1() {
        return "get one";
    }

    @GetMapping("/get2")
    public String get2() {
        return "get two";
    }
}

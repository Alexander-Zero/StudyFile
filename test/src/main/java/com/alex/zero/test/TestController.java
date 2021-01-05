package com.alex.zero.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/27
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "这是一个测试的";
    }
}

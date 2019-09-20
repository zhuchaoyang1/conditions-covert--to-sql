package com.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 集成测试
 * @Author: ${朱朝阳}
 * @Date: 2019/9/18 23:48
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/test/{query}/{entityName}")
    public String test(@PathVariable String query,@PathVariable String entityName) {
        return query;
    }

}

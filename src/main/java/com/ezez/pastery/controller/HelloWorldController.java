package com.ezez.pastery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class HelloWorldController {

    @GetMapping("/hello" )
    public String firstPage() {
        return "Hello World Ezugworie";
    }

}

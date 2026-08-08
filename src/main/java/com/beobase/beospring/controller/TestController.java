package com.beobase.beospring.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://beobase.com",
        "https://www.beobase.com"
})
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "Ok message from Beo Spring";
    }
}

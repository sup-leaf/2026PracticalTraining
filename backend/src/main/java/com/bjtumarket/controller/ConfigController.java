package com.bjtumarket.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ConfigController {

    @GetMapping("/config")
    public Map<String, String> config() {
        Map<String, String> result = new HashMap<>();
        result.put("API_BASE_URL", "/api");
        return result;
    }
}


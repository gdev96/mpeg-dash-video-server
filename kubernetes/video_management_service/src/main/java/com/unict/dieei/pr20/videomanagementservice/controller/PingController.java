package com.unict.dieei.pr20.videomanagementservice.controller;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
public class PingController {

    @GetMapping(path = "/ping")
    public @ResponseBody ResponseEntity<String> ping() {
        return new ResponseEntity<>("Pong", HttpStatus.OK);
    }
}

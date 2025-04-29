package org.example.tulparelectric.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RobotsController {

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getRobotsTxt() {
        return "User-agent: *\nDisallow:";
    }
}


package org.example.tulparelectric.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IpController {
    @GetMapping("/ip-check")
    @ResponseBody
    public String ipCheck(HttpServletRequest request) {
        return String.format(
                "Remote IP: %s\nX-Real-IP: %s\nX-Forwarded-For: %s",
                request.getRemoteAddr(),
                request.getHeader("X-Real-IP"),
                request.getHeader("X-Forwarded-For")
        );
    }
}

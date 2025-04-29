package org.example.tulparelectric.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CooperationController {

    @GetMapping("/cooperation")
    public String showCooperationPage() {
        return "cooperation";
    }
}

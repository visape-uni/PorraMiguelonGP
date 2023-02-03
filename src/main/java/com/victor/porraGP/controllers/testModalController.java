package com.victor.porraGP.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class testModalController {

    @GetMapping("/testModal")
    public String getModal() {
        return "testModal";
    }
}

package com.victor.porraGP.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RulesController {

    @RequestMapping("/rules")
    public String getRules() {
        return "rules";
    }
}

package com.victor.porraGP.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BetController {

    @GetMapping("/make-bet")
    public String getMakeBet() {
        return "make_bet";
    }

    @PostMapping("/make-bet")
    public String makeBet(Model model) {
        model.addAttribute("", "");
        return "make_bet";
    }
}

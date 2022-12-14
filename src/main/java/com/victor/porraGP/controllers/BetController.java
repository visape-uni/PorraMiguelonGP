package com.victor.porraGP.controllers;

import com.victor.porraGP.dto.BetDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class BetController {

    @GetMapping("/make-bet")
    public String getMakeBet(Model model) {
        model.addAttribute("bet", new BetDto());
        return "make-bet";
    }

    @PostMapping("/make-bet")
    public ModelAndView makeBet(@Valid @ModelAttribute("bet") BetDto betDto,
                          Errors errors, ModelAndView modelAndView) {
        if (!errors.hasErrors()) {
            //TODO: SAVE BET
        }
        return modelAndView;
    }
}

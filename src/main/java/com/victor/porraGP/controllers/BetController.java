package com.victor.porraGP.controllers;

import com.victor.porraGP.dto.BetDto;
import com.victor.porraGP.services.BetService;
import com.victor.porraGP.services.RaceService;
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

    private final BetService betService;
    private final RaceService raceService;

    public BetController(BetService betService, RaceService raceService) {
        this.betService = betService;
        this.raceService = raceService;
    }

    @GetMapping("/make-bet")
    public String getMakeBet(Model model) {
        //model.addAttribute("nextRace", raceService.findNextRace());
        BetDto betDto = new BetDto();
        betDto.setRace(raceService.findNextRace());
        model.addAttribute("bet", betDto);
        return "make-bet";
    }

    @PostMapping("/make-bet")
    public ModelAndView makeBet(@Valid @ModelAttribute("bet") BetDto betDto,
                          Errors errors, ModelAndView modelAndView) {
        if (!errors.hasErrors()) {
            BetDto bet = betService.saveBet(betDto);
        }
        return modelAndView;
    }
}

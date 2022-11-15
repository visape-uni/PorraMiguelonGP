package com.victor.porraGP.controllers;

import com.victor.porraGP.services.RaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CalendarController {
    private final RaceService raceService;

    public CalendarController(RaceService raceService) {
        this.raceService = raceService;
    }

    @RequestMapping("/calendar")
    public String getCalendar(Model model) {
        model.addAttribute("races", raceService.getAllRaces(false));
        return "calendar";
    }
}

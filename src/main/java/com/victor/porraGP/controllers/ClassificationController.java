package com.victor.porraGP.controllers;

import com.victor.porraGP.services.ClassificationService;
import com.victor.porraGP.services.RaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClassificationController {
    public static final int SEASON = 2023;

    private final ClassificationService classificationService;
    private final RaceService raceService;

    public ClassificationController(ClassificationService classificationService, RaceService raceService) {
        this.classificationService = classificationService;
        this.raceService = raceService;
    }

    @RequestMapping("/classification")
    public String getClassification(Model model, @RequestParam("race") Long classificationId) {
        model.addAttribute("currentClassification", classificationService.findClassificationByRace(classificationId));
        model.addAttribute("races", raceService.getAllRacesBySeason(SEASON));
        return "classification";
    }
}

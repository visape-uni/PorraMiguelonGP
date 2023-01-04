package com.victor.porraGP.controllers;

import com.victor.porraGP.services.ClassificationService;
import com.victor.porraGP.services.RaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.victor.porraGP.services.impl.RaceServiceImpl.SEASON_2023;

@Controller
public class ClassificationController {
    private final ClassificationService classificationService;
    private final RaceService raceService;

    public ClassificationController(ClassificationService classificationService, RaceService raceService) {
        this.classificationService = classificationService;
        this.raceService = raceService;
    }

    @RequestMapping("/classification")
    public String getClassification(Model model, @RequestParam("race") Long classificationId) {
        model.addAttribute("currentClassification", classificationService.findClassificationByRace(classificationId));
        model.addAttribute("races", raceService.getAllRacesBySeason(SEASON_2023, true));
        return "classification";
    }
}

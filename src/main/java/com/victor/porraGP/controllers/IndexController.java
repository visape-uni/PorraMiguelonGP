package com.victor.porraGP.controllers;

import com.victor.porraGP.services.ClassificationService;
import com.victor.porraGP.services.RaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    private final RaceService raceService;
    private final ClassificationService classificationService;

    private static final Long ID_GENERAL_CLASSIFICATION = 0L;

    public IndexController(RaceService raceService, ClassificationService classificationService) {
        this.raceService = raceService;
        this.classificationService = classificationService;
    }

    @RequestMapping({"", "/", "index", "index.html", "home", "home.html"})
    public String index(Model model){
        model.addAttribute("classification", classificationService.findClassificationByRace(ID_GENERAL_CLASSIFICATION));
        model.addAttribute("nextRace", raceService.findNextRace());
        return "index";
    }
}

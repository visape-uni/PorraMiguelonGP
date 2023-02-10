package com.victor.porraGP.controllers;

import com.victor.porraGP.dto.BetDto;
import com.victor.porraGP.dto.RaceDto;
import com.victor.porraGP.dto.RiderDto;
import com.victor.porraGP.services.BetService;
import com.victor.porraGP.services.RaceService;
import com.victor.porraGP.services.RiderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;

import static com.victor.porraGP.services.impl.BetServiceImpl.*;
import static com.victor.porraGP.services.impl.RaceServiceImpl.SEASON_2023;

@Controller
public class AdminController {
    private final RaceService raceService;
    private final BetService betService;
    private final RiderService riderService;

    public AdminController(RaceService raceService, BetService betService, RiderService riderService) {
        this.raceService = raceService;
        this.betService = betService;
        this.riderService = riderService;
    }

    @RequestMapping("/admin")
    public String getAdminPage(Model model, @RequestParam(value = "race", required = false) Long raceId) {
        BetDto betDto = new BetDto();
        model.addAttribute("result", betDto);
        addAttributesToModel(model, raceId);
        return "admin";
    }

    @PostMapping("/admin")
    public Model postResult(@Valid @ModelAttribute("result") BetDto betDto,
                         Errors errors, Model model) {
        if (!errors.hasErrors()) {
            String validationError = betService.validateAndCompleteBet(betDto);
            if (!StringUtils.hasText(validationError)) {
                BetDto bet = betService.saveResult(betDto);
            } else {
                model.addAttribute("validationError", validationError);
            }
        }
        addAttributesToModel(model, betDto.getRaceId());
        return model;
    }

    @PostMapping("/admin/close-race")
    public String closeRace(@RequestParam(value = "race") Long raceId) {
        raceService.closeRace(raceId);
        return "redirect:/admin";
    }

    @PostMapping("/admin/open-race")
    public String openRace(@RequestParam(value = "race") Long raceId) {
        raceService.openRace(raceId);
        return "redirect:/admin";
    }

    private void addAttributesToModel(Model model, Long raceId) {
        model.addAttribute("races", raceService.getAllRacesBySeason(SEASON_2023, false));

        RaceDto race = (raceId != null) ? raceService.findRace(raceId) : raceService.findNextRace();
        model.addAttribute("race", race);
        model.addAttribute("existingResult", betService.findResult(race.getId()));

        Map<String, List<RiderDto>> ridersMap = riderService.getAllRiders();
        model.addAttribute("ridersMoto3", ridersMap.get(MOTO_3_CATEGORY));
        model.addAttribute("ridersMoto2", ridersMap.get(MOTO_2_CATEGORY));
        model.addAttribute("ridersMotoGP", ridersMap.get(MOTO_GP_CATEGORY));
    }
}

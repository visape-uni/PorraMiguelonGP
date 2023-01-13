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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.victor.porraGP.services.impl.BetServiceImpl.*;

@Controller
public class BetController {

    private static final String ERROR_BET_CLOSED = "error.betIsClosed";
    private final BetService betService;
    private final RaceService raceService;
    private final RiderService riderService;
    public BetController(BetService betService, RaceService raceService, RiderService riderService) {
        this.betService = betService;
        this.raceService = raceService;
        this.riderService = riderService;
    }

    @GetMapping("/make-bet")
    public String getMakeBet(Model model) {
        addAttributesToModel(model);
        BetDto betDto = new BetDto();
        model.addAttribute("bet", betDto);
        return "make-bet";
    }

    @PostMapping("/make-bet")
    public Model makeBet(@Valid @ModelAttribute("bet") BetDto betDto,
                                Errors errors, Model model) {
        if (!errors.hasErrors()) {
            String validationError = betService.validateAndCompleteBet(betDto);
            if (!StringUtils.hasText(validationError)) {
                RaceDto race = raceService.findRace(betDto.getRaceId());
                if (race.isOpen()) {
                    BetDto bet = betService.saveBet(betDto);
                } else {
                    model.addAttribute("validationError", ERROR_BET_CLOSED);
                }
            } else {
                model.addAttribute("validationError", validationError);
            }
        }
        addAttributesToModel(model);
        return model;
    }

    private void addAttributesToModel(Model model) {
        RaceDto nextRace = raceService.findNextRace();
        model.addAttribute("nextRace", nextRace);
        BetDto existingBet = betService.findBet(nextRace.getId());
        model.addAttribute("existingBet", existingBet);
        Map<String, List<RiderDto>> ridersMap = riderService.getAllRiders();
        model.addAttribute("ridersMoto3", ridersMap.get(MOTO_3_CATEGORY));
        model.addAttribute("ridersMoto2", ridersMap.get(MOTO_2_CATEGORY));
        model.addAttribute("ridersMotoGP", ridersMap.get(MOTO_GP_CATEGORY));
    }
}
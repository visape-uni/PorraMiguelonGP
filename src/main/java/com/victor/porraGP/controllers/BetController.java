package com.victor.porraGP.controllers;

import com.victor.porraGP.dto.BetDto;
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
        //model.addAttribute("nextRace", raceService.findNextRace());
        addRidersToModel(model);
        BetDto betDto = new BetDto();
        betDto.setRace(raceService.findNextRace());
        model.addAttribute("bet", betDto);
        return "make-bet";
    }

    @PostMapping("/make-bet")
    public Model makeBet(@Valid @ModelAttribute("bet") BetDto betDto,
                                //@ModelAttribute("riders") List<RiderDto> riders,
                                Errors errors, Model model) {
        if (!errors.hasErrors()) {
            String validationError = betService.validateAndCompleteBet(betDto);
            if (!StringUtils.hasText(validationError)) {
                BetDto bet = betService.saveBet(betDto);
                //TODO: SHOW SUCCESS MESSAGE
            } else {
                model.addAttribute("validationError", validationError);
            }
        }
        addRidersToModel(model);
        return model;
    }

    private void addRidersToModel(Model model) {
        Map<String, List<RiderDto>> ridersMap = riderService.getAllRiders();
        model.addAttribute("ridersMoto3", ridersMap.get(MOTO_3_CATEGORY));
        model.addAttribute("ridersMoto2", ridersMap.get(MOTO_2_CATEGORY));
        model.addAttribute("ridersMotoGP", ridersMap.get(MOTO_GP_CATEGORY));
    }

}

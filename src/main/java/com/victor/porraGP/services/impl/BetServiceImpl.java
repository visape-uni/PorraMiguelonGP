package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.BetDto;
import com.victor.porraGP.model.Bet;
import com.victor.porraGP.model.ClassifiedTeam;
import com.victor.porraGP.model.User;
import com.victor.porraGP.repositories.BetRepository;
import com.victor.porraGP.repositories.ClassificationRepository;
import com.victor.porraGP.services.BetService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BetServiceImpl implements BetService {
    private final ClassificationRepository classificationRepository;
    private final BetRepository betRepository;

    public BetServiceImpl(ClassificationRepository classificationRepository, BetRepository betRepository) {
        this.classificationRepository = classificationRepository;
        this.betRepository = betRepository;
    }

    @Override
    public BetDto saveBet(BetDto betDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClassifiedTeam classifiedTeam = classificationRepository
                .findClassifiedTeamByRaceIdAndTeamId(betDto.getRace().getId(), user.getTeam().getId());

        Bet bet = betRepository.save(createBet(betDto, classifiedTeam));
        classifiedTeam.setBet(bet);
        classificationRepository.save(classifiedTeam);

        return betDto;
    }

    private Bet createBet(BetDto betDto, ClassifiedTeam classifiedTeam) {
        Bet bet = new Bet();
        bet.setMoto3(betDto.getMoto3());
        bet.setMoto2(betDto.getMoto2());
        bet.setMotogpFirst(betDto.getMotogpFirst());
        bet.setMotogpSecond(betDto.getMotogpSecond());
        bet.setMotogpThird(betDto.getMotogpThird());
        bet.setMotogpForth(betDto.getMotogpForth());
        bet.setMotogpFifth(betDto.getMotogpFifth());
        bet.setMotogpSixth(betDto.getMotogpSixth());
        bet.setClassifiedTeam(classifiedTeam);
        return bet;
    }
}

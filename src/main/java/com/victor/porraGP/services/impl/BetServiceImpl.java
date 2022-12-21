package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.BetDto;
import com.victor.porraGP.model.*;
import com.victor.porraGP.repositories.BetRepository;
import com.victor.porraGP.repositories.ClassificationRepository;
import com.victor.porraGP.repositories.RiderRepository;
import com.victor.porraGP.services.BetService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BetServiceImpl implements BetService {
    public static final String MOTO_3_CATEGORY = "Moto3";
    public static final String MOTO_2_CATEGORY = "Moto2";
    public static final String MOTO_GP_CATEGORY = "MotoGP";

    private static final String ERROR_RIDER_NOT_FOUND = "error.riderNotFound";
    private static final String ERROR_RIDER_DUPLICATED = "error.riderDuplicated";

    private final ClassificationRepository classificationRepository;
    private final BetRepository betRepository;
    private final RiderRepository riderRepository;

    public BetServiceImpl(ClassificationRepository classificationRepository, BetRepository betRepository, RiderRepository riderRepository) {
        this.classificationRepository = classificationRepository;
        this.betRepository = betRepository;
        this.riderRepository = riderRepository;
    }

    @Override
    public BetDto saveBet(BetDto betDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClassifiedTeam classifiedTeam = classificationRepository
                .findClassifiedTeamByRaceIdAndTeamId(betDto.getRaceId(), user.getTeam().getId());

        Bet bet = betRepository.save(createBet(betDto, classifiedTeam));
        classifiedTeam.setBet(bet);
        classificationRepository.save(classifiedTeam);

        return betDto;
    }

    @Override
    public String validateAndCompleteBet(BetDto betDto) {
        List<RiderId> riderIdList = List.of(
                new RiderId(Long.valueOf(betDto.getMoto3()), MOTO_3_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMoto2()), MOTO_2_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpFirst()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpSecond()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpThird()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpForth()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpFifth()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpSixth()), MOTO_GP_CATEGORY)
        );
        List<RiderId> riderIdListWithoutDuplicated = riderIdList.stream().distinct().collect(Collectors.toList());
        if (riderIdListWithoutDuplicated.size() != riderIdList.size()) {
            return ERROR_RIDER_DUPLICATED;
        }

        List<Rider> riders = (List<Rider>) riderRepository.findAllById(riderIdList);
        if (riders.size() != 8) {
            return ERROR_RIDER_NOT_FOUND;
        }

        return null;
    }

    @Override
    public BetDto findBet(Long raceId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClassifiedTeam classifiedTeam = classificationRepository.findClassifiedTeamByRaceIdAndTeamId(raceId, user.getTeam().getId());
        BetDto betDto = null;
        if (!Objects.isNull(classifiedTeam) && !Objects.isNull(classifiedTeam.getBet())) {
            betDto = new BetDto(raceId, classifiedTeam.getBet().getMoto3(), classifiedTeam.getBet().getMoto2(),
                    classifiedTeam.getBet().getMotogpFirst(), classifiedTeam.getBet().getMotogpSecond(),
                    classifiedTeam.getBet().getMotogpThird(), classifiedTeam.getBet().getMotogpForth(),
                    classifiedTeam.getBet().getMotogpFifth(), classifiedTeam.getBet().getMotogpSixth());
        }
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

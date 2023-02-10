package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.BetDto;
import com.victor.porraGP.model.*;
import com.victor.porraGP.repositories.*;
import com.victor.porraGP.services.BetService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BetServiceImpl implements BetService {
    // Categories
    public static final String MOTO_3_CATEGORY = "Moto3";
    public static final String MOTO_2_CATEGORY = "Moto2";
    public static final String MOTO_GP_CATEGORY = "MotoGP";
    // Errors
    private static final String ERROR_RIDER_NOT_FOUND = "error.riderNotFound";
    private static final String ERROR_RIDER_DUPLICATED = "error.riderDuplicated";
    private static final String ERROR_RACE_IS_CLOSED = "error.raceClosed";
    // Points
    private static final Integer MOTO3_POINTS = 10;
    private static final Integer MOTO2_POINTS = 10;
    private static final Integer MOTOGP_FIRST_POINTS = 10;
    private static final Integer MOTOGP_SECOND_POINTS = 11;
    private static final Integer MOTOGP_THIRD_POINTS = 12;
    private static final Integer MOTOGP_FOURTH_POINTS = 13;
    private static final Integer MOTOGP_FIFTH_POINTS = 14;
    private static final Integer MOTOGP_SIXTH_POINTS = 15;
    private static final Integer MOTOGP_PRESENT_POINTS = 5;
    private static final Integer UNIQUE_RIDER_POINTS = 25;
    private static final Integer ALL_MOTOGP_RIDERS_POINTS = 25;
    private static final Integer ALL_RIDERS_POINTS = 25;
    // Money
    private static final Integer FIRST_MONEY = 50;
    private static final Integer SECOND_MONEY = 25;
    private static final Integer THIRD_MONEY = 20;
    private static final Integer FOURTH_MONEY = 5;
    // Repositories
    private final RaceRepository raceRepository;
    private final BetRepository betRepository;
    private final RiderRepository riderRepository;
    private final ClassificationRepository classificationRepository;
    private final ConfigurationRepository configurationRepository;
    public BetServiceImpl(RaceRepository raceRepository, BetRepository betRepository, RiderRepository riderRepository, ClassificationRepository classificationRepository, ConfigurationRepository configurationRepository) {
        this.raceRepository = raceRepository;
        this.betRepository = betRepository;
        this.riderRepository = riderRepository;
        this.classificationRepository = classificationRepository;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public BetDto saveResult(BetDto betDto) {
        Bet existingBet = betRepository.findBetByRaceIdAndTeamId(betDto.getRaceId(), null);
        Bet bet;
        if (existingBet != null) {
            bet = betRepository.save(modifyResult(betDto, existingBet));
        } else {
            bet = betRepository.save(createResult(betDto));
        }
        calculatePoints(bet);
        return betDto;
    }
    private void calculatePoints(Bet result) {
        List<Bet> bets = betRepository.findBetsByRaceId(result.getRace().getId()).stream().filter(b -> !b.getResult()).collect(Collectors.toList());
        List<ClassifiedTeam> classifiedTeams = classificationRepository.findClassificationsByRaceId(result.getRace().getId());
        List<ClassifiedTeam> generalTeams = classificationRepository.findClassificationsByRaceId(0L);
        // See if there is any unique pilot and set it to uniqueRidersList
        Map<String, Integer> allRidersMap = createUniqueRidersList(bets);
        List<String> uniqueRidersList = new ArrayList<>();
        if (allRidersMap.containsValue(1)) {
            uniqueRidersList = allRidersMap.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), 1))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
        }

        Map<Integer, String> motoGpResults = Map.of(
                1, result.getMotogpFirst(),
                2, result.getMotogpSecond(),
                3, result.getMotogpThird(),
                4, result.getMotogpFourth(),
                5, result.getMotogpFifth(),
                6, result.getMotogpSixth()
        );

        for(Bet bet : bets) {
            ClassifiedTeam classifiedTeam = classifiedTeams.stream().filter(team -> team.getTeam().equals(bet.getTeam())
                    && team.getRace().equals(bet.getRace())).findFirst().get();
            setAllPoints(result, uniqueRidersList, motoGpResults, bet, classifiedTeam);
        }
        // Order according Gp points
        classifiedTeams.sort(Comparator.comparing(ClassifiedTeam::getTotalGpPoints).reversed());
        // Set Position GP
        calculateGpPositions(classifiedTeams);
        // Set Money
        calculateMoney(classifiedTeams);
        // Order according total points
        classifiedTeams.sort(Comparator.comparing(ClassifiedTeam::getTotalPoints).reversed());
        // Set Position final
        calculatePositions(classifiedTeams);
        // Save Race Classification
        classificationRepository.saveAll(classifiedTeams);

        addToGeneralClassification(generalTeams);
        // Save General Classification
        classificationRepository.saveAll(generalTeams);
    }

    private void addToGeneralClassification(List<ClassifiedTeam> generalTeams) {
        List<ClassifiedTeam> notGeneralRaceTeamsWithPointsOrEarned =
                classificationRepository.findClassificationsByRaceIdIsNotAndHasPointsOrEarned(0L);
        Map<Team, List<ClassifiedTeam>> mapOfTeams =
                notGeneralRaceTeamsWithPointsOrEarned.stream().collect(Collectors.groupingBy(ClassifiedTeam::getTeam));
        mapOfTeams.forEach((teamFromMap, classifications) -> {
            int totalTeamPoints = classifications.stream()
                    .reduce(0, (subtotal, element) -> subtotal + element.getTotalPoints(), Integer::sum);
            double totalTeamEarned = classifications.stream()
                    .reduce(0.0, (subtotal, element) -> subtotal + element.getEarned(), Double::sum);

            ClassifiedTeam generalTeam = generalTeams.stream()
                    .filter(generalClassifiedTeam -> generalClassifiedTeam.getTeam().equals(teamFromMap))
                    .findFirst().get();
            generalTeam.setTotalPoints(totalTeamPoints);
            generalTeam.setEarned(totalTeamEarned);
        });
        // Order according total points
        generalTeams.sort(Comparator.comparing(ClassifiedTeam::getTotalPoints).reversed());
        calculatePositions(generalTeams);
    }
    private void calculateMoney(List<ClassifiedTeam> classifiedTeams) {
        final int RACE_PRICE = Integer.parseInt(configurationRepository
                .findConfigurationByClave("RACE_PRICE").getValor());
        int awardedTeams = 0;
        Map<Integer, List<ClassifiedTeam>> podiumMap = classifiedTeams.stream()
                .filter(BetServiceImpl::isaPodium)
                .collect(Collectors.groupingBy(ClassifiedTeam::getPosition));

        List<ClassifiedTeam> firstTeams = podiumMap.get(1);
        if (firstTeams!= null && firstTeams.size() == 1) {
            setEarnedMoney(firstTeams, FIRST_MONEY*RACE_PRICE/100);
            awardedTeams += 1;
        } else if (firstTeams != null && firstTeams.size() == 2) {
            int givenMoney = (FIRST_MONEY + SECOND_MONEY)*RACE_PRICE/100;
            setEarnedMoney(firstTeams, givenMoney);
            awardedTeams += 2;
        } else if (firstTeams != null && firstTeams.size() == 3) {
            int givenMoney = (FIRST_MONEY + SECOND_MONEY + THIRD_MONEY)*RACE_PRICE/100;
            setEarnedMoney(firstTeams, givenMoney);
            awardedTeams += 3;
        } else if (firstTeams != null && firstTeams.size() > 3) {
            int givenMoney = (FIRST_MONEY + SECOND_MONEY + THIRD_MONEY + FOURTH_MONEY)*RACE_PRICE/100;
            setEarnedMoney(firstTeams, givenMoney);
            awardedTeams += firstTeams.size();
        }
        if (awardedTeams == 1) {
            List<ClassifiedTeam> secondTeams = podiumMap.get(2);
            if (secondTeams != null && secondTeams.size() == 1) {
                setEarnedMoney(secondTeams, SECOND_MONEY*RACE_PRICE/100);
                awardedTeams += 1;
            } else if (secondTeams != null && secondTeams.size() == 2) {
                int givenMoney = (SECOND_MONEY + THIRD_MONEY)*RACE_PRICE/100;
                setEarnedMoney(secondTeams, givenMoney);
                awardedTeams += 2;
            } else if (secondTeams != null && secondTeams.size() > 2){
                int givenMoney = (SECOND_MONEY + THIRD_MONEY + FOURTH_MONEY)*RACE_PRICE/100;
                setEarnedMoney(secondTeams, givenMoney);
                awardedTeams += secondTeams.size();
            }
        }
        if (awardedTeams == 2) {
            List<ClassifiedTeam> thirdTeams = podiumMap.get(3);
            if (thirdTeams != null && thirdTeams.size() == 1) {
                setEarnedMoney(thirdTeams, THIRD_MONEY*RACE_PRICE/100);
                awardedTeams += 1;
            } else if(thirdTeams != null && thirdTeams.size() > 1) {
                setEarnedMoney(thirdTeams, (THIRD_MONEY + FOURTH_MONEY)*RACE_PRICE/100);
                awardedTeams += thirdTeams.size();
            }
        }
        if (awardedTeams == 3) {
            List<ClassifiedTeam> fourthTeams = podiumMap.get(4);
            if (fourthTeams != null && fourthTeams.size() == 1) {
                setEarnedMoney(fourthTeams, FOURTH_MONEY*RACE_PRICE/100);
                awardedTeams += fourthTeams.size();
            }
        }
    }
    private static void setEarnedMoney(List<ClassifiedTeam> teams, double givenMoney) {
        if (teams != null) {
            teams.forEach(classifiedTeam -> classifiedTeam.setEarned(givenMoney / teams.size()));
        }
    }

    private static boolean isaPodium(ClassifiedTeam classifiedTeam) {
        return classifiedTeam.getPosition().equals(1) || classifiedTeam.getPosition().equals(2)
                || classifiedTeam.getPosition().equals(3) || classifiedTeam.getPosition().equals(4);
    }
    private static void calculateGpPositions(List<ClassifiedTeam> classifiedTeams) {
        int position = 1;
        int positionSkipped = 1;
        int pointsLast = -1;
        for(ClassifiedTeam classifiedTeam : classifiedTeams) {
            if (pointsLast == classifiedTeam.getTotalGpPoints()) {
                classifiedTeam.setPosition(position - positionSkipped);
                ++positionSkipped;
            } else {
                classifiedTeam.setPosition(position);
                pointsLast = classifiedTeam.getTotalGpPoints();
                positionSkipped = 1;
            }
            ++position;
        }
    }
    private static void calculatePositions(List<ClassifiedTeam> classifiedTeams) {
        int position = 1;
        int positionSkipped = 1;
        int pointsLast = -1;
        for(ClassifiedTeam classifiedTeam : classifiedTeams) {
            if (pointsLast == classifiedTeam.getTotalPoints()) {
                classifiedTeam.setPosition(position - positionSkipped);
                ++positionSkipped;
            } else {
                classifiedTeam.setPosition(position);
                pointsLast = classifiedTeam.getTotalPoints();
                positionSkipped = 1;
            }
            ++position;
        }
    }
    private static void setAllPoints(Bet result, List<String> uniqueRidersList, Map<Integer, String> motoGpResults,
                                     Bet bet, ClassifiedTeam classifiedTeam) {
        int moto2And3Points = 0;
        MotoGpPoints motoGpPoints = new MotoGpPoints();
        int totalGpPoints;
        int totalPoints;
        List<Integer> rightPositions = new ArrayList<>();
        moto2And3Points += addPointsFromRider(bet.getMoto3(), 7, rightPositions, result.getMoto3(), MOTO3_POINTS);
        moto2And3Points += addPointsFromRider(bet.getMoto2(), 8, rightPositions, result.getMoto2(), MOTO2_POINTS);

        addPointsFromGpRider(bet.getMotogpFirst(), 1, rightPositions, motoGpResults, uniqueRidersList,
                MOTOGP_FIRST_POINTS, motoGpPoints);
        addPointsFromGpRider(bet.getMotogpSecond(), 2, rightPositions, motoGpResults, uniqueRidersList,
                MOTOGP_SECOND_POINTS, motoGpPoints);
        addPointsFromGpRider(bet.getMotogpThird(), 3, rightPositions, motoGpResults, uniqueRidersList,
                MOTOGP_THIRD_POINTS, motoGpPoints);
        addPointsFromGpRider(bet.getMotogpFourth(), 4, rightPositions, motoGpResults, uniqueRidersList,
                MOTOGP_FOURTH_POINTS, motoGpPoints);
        addPointsFromGpRider(bet.getMotogpFifth(), 5, rightPositions, motoGpResults, uniqueRidersList,
                MOTOGP_FIFTH_POINTS, motoGpPoints);
        addPointsFromGpRider(bet.getMotogpSixth(), 6, rightPositions, motoGpResults, uniqueRidersList,
                MOTOGP_SIXTH_POINTS, motoGpPoints);

        addBonusPointsForAllRiders(rightPositions, motoGpPoints);

        //Set points
        moto2And3Points += motoGpPoints.bonificationMoto3And2Points; // La bonificacion de acertar moto 3, moto 2 y moto gp se suma en los puntos de moto 2 y moto 3
        totalGpPoints = motoGpPoints.entryPoints + motoGpPoints.positionPoints + motoGpPoints.bonificationPoints;
        totalPoints = moto2And3Points + totalGpPoints;
        classifiedTeam.setMotoTwoAndThreePoints(moto2And3Points);
        classifiedTeam.setEntryPoints(motoGpPoints.entryPoints);
        classifiedTeam.setPositionPoints(motoGpPoints.positionPoints);
        classifiedTeam.setBonificationGpPoints(motoGpPoints.bonificationPoints);
        classifiedTeam.setTotalGpPoints(totalGpPoints);
        classifiedTeam.setTotalPoints(totalPoints);
    }

    private static void addBonusPointsForAllRiders(List<Integer> rightPositions, MotoGpPoints motoGpPoints) {
        if (rightPositions.size() >= 6) {
            if (rightPositions.size() == 8) {
                motoGpPoints.bonificationMoto3And2Points += ALL_RIDERS_POINTS;
                motoGpPoints.bonificationPoints += ALL_MOTOGP_RIDERS_POINTS;
            } else if(!rightPositions.contains(7) && !rightPositions.contains(8)) {
                motoGpPoints.bonificationPoints += ALL_MOTOGP_RIDERS_POINTS;
            }
        }
    }
    private static void addPointsFromGpRider(String betRider, Integer betPosition, List<Integer> rightPositions,
                                             Map<Integer, String> motoGpResult, List<String> uniqueRidersList,
                                             Integer riderPositionPoints, MotoGpPoints motoGpPoints) {
        if (motoGpResult.containsValue(betRider)) {
            motoGpPoints.entryPoints += MOTOGP_PRESENT_POINTS;
            rightPositions.add(betPosition);
            if (motoGpResult.get(betPosition).equals(betRider)) {
                motoGpPoints.positionPoints += riderPositionPoints;
            }
            if (uniqueRidersList.size() > 0 && uniqueRidersList.contains(betRider)) {
                motoGpPoints.bonificationPoints += UNIQUE_RIDER_POINTS;
                uniqueRidersList.remove(betRider);
            }
        }
    }
    private static int addPointsFromRider(String betRider, Integer betPosition, List<Integer> rightPositions,
                                           String resultRider, Integer riderPoints) {
        if (betRider.equals(resultRider)) {
            rightPositions.add(betPosition);
            return riderPoints;
        }
        return 0;
    }
    // Returns a map with Key = riderId and Value = number of times it appears
    private Map<String, Integer> createUniqueRidersList(List<Bet> bets) {
        Map<String, Integer> uniqueMotoGpRiders = new HashMap<>();
        bets.forEach(bet -> {
            addToUniqueRidersList(bet.getMotogpFirst(), uniqueMotoGpRiders);
            addToUniqueRidersList(bet.getMotogpSecond(), uniqueMotoGpRiders);
            addToUniqueRidersList(bet.getMotogpThird(), uniqueMotoGpRiders);
            addToUniqueRidersList(bet.getMotogpFourth(), uniqueMotoGpRiders);
            addToUniqueRidersList(bet.getMotogpFifth(), uniqueMotoGpRiders);
            addToUniqueRidersList(bet.getMotogpSixth(), uniqueMotoGpRiders);
        });
        return uniqueMotoGpRiders;
    }
    private void addToUniqueRidersList(String riderId, Map<String, Integer> uniqueRidersList) {
        if (uniqueRidersList.containsKey(riderId)) {
            uniqueRidersList.merge(riderId, 1, Integer::sum);
        } else {
            uniqueRidersList.put(riderId, 1);
        }
    }

    @Override
    public BetDto findResult(Long raceId) {
        Bet bet = betRepository.findBetByRaceIdAndTeamId(raceId, null);
        BetDto betDto = null;
        if (!Objects.isNull(bet)) {
            betDto = new BetDto(raceId, bet.getMoto3(), bet.getMoto2(), bet.getMotogpFirst(), bet.getMotogpSecond(),
                    bet.getMotogpThird(), bet.getMotogpFourth(), bet.getMotogpFifth(), bet.getMotogpSixth());
        }
        return betDto;
    }

    @Override
    public BetDto saveBet(BetDto betDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Race> race = raceRepository.findById(betDto.getRaceId());
        if (race.isPresent()) {
            Bet existingbet = betRepository.findBetByRaceIdAndTeamId(race.get().getId(), user.getTeam().getId());
            if (existingbet != null) {
                betRepository.save(modifyBet(betDto, user.getTeam(), race.get(), existingbet));
            } else {
                betRepository.save(createBet(betDto, user.getTeam(), race.get()));
            }
        } else {
            throw new RuntimeException("Race was not found");
        }

        return betDto;
    }

    @Override
    public String validateAndCompleteBet(BetDto betDto, boolean result) {
        if (!result) {
            Optional<Race> optionalRace = raceRepository.findById(betDto.getRaceId());
            if (optionalRace.isEmpty() || !optionalRace.get().isOpen()) {
                return ERROR_RACE_IS_CLOSED;
            }
        }

        List<RiderId> riderIdList = List.of(
                new RiderId(Long.valueOf(betDto.getMoto3()), MOTO_3_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMoto2()), MOTO_2_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpFirst()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpSecond()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpThird()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpFourth()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpFifth()), MOTO_GP_CATEGORY),
                new RiderId(Long.valueOf(betDto.getMotogpSixth()), MOTO_GP_CATEGORY)
        );
        List<RiderId> riderIdListWithoutDuplicated = riderIdList.stream().distinct().toList();
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
        Bet bet = betRepository.findBetByRaceIdAndTeamId(raceId, user.getTeam().getId());
        BetDto betDto = null;
        if (!Objects.isNull(bet)) {
            betDto = new BetDto(raceId, bet.getMoto3(), bet.getMoto2(), bet.getMotogpFirst(), bet.getMotogpSecond(),
                    bet.getMotogpThird(), bet.getMotogpFourth(), bet.getMotogpFifth(), bet.getMotogpSixth());
        }
        return betDto;
    }

    private Bet modifyResult(BetDto betDto, Bet existingResult) {
        fillBetWithPilots(betDto, existingResult);
        existingResult.setResult(true);
        Race race = new Race();
        race.setId(betDto.getRaceId());
        existingResult.setRace(race);
        return existingResult;
    }
    private Bet createResult(BetDto betDto) {
        Bet bet = new Bet();
        fillBetWithPilots(betDto, bet);
        bet.setResult(true);

        Race race = new Race();
        race.setId(betDto.getRaceId());
        bet.setRace(race);
        return bet;
    }
    private Bet modifyBet(BetDto betDto, Team team, Race race, Bet existingBet) {
        fillBetWithPilots(betDto, existingBet);
        existingBet.setTeam(team);
        existingBet.setRace(race);
        existingBet.setResult(false);
        return existingBet;
    }
    private Bet createBet(BetDto betDto, Team team, Race race) {
        Bet bet = new Bet();
        fillBetWithPilots(betDto, bet);
        bet.setTeam(team);
        bet.setRace(race);
        bet.setResult(false);
        return bet;
    }
    private static void fillBetWithPilots(BetDto betDto, Bet bet) {
        bet.setMoto3(betDto.getMoto3());
        bet.setMoto2(betDto.getMoto2());
        bet.setMotogpFirst(betDto.getMotogpFirst());
        bet.setMotogpSecond(betDto.getMotogpSecond());
        bet.setMotogpThird(betDto.getMotogpThird());
        bet.setMotogpFourth(betDto.getMotogpFourth());
        bet.setMotogpFifth(betDto.getMotogpFifth());
        bet.setMotogpSixth(betDto.getMotogpSixth());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class MotoGpPoints {
        private int entryPoints;
        private int positionPoints;
        private int bonificationPoints;
        private int bonificationMoto3And2Points;
    }
}

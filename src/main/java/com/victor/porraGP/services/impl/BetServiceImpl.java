package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.BetDto;
import com.victor.porraGP.model.*;
import com.victor.porraGP.repositories.BetRepository;
import com.victor.porraGP.repositories.ClassificationRepository;
import com.victor.porraGP.repositories.RaceRepository;
import com.victor.porraGP.repositories.RiderRepository;
import com.victor.porraGP.services.BetService;
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
    // Points
    private static final Integer MOTO3_POINTS = 10;
    private static final Integer MOTO2_POINTS = 10;
    private static final Integer MOTOGP_FIRST_POINTS = 10;
    private static final Integer MOTOGP_SECOND_POINTS = 10;
    private static final Integer MOTOGP_THIRD_POINTS = 10;
    private static final Integer MOTOGP_FOURTH_POINTS = 10;
    private static final Integer MOTOGP_FIFTH_POINTS = 10;
    private static final Integer MOTOGP_SIXTH_POINTS = 10;
    private static final Integer MOTOGP_PRESENT_POINTS = 10;
    private static final Integer UNIQUE_RIDER_POINTS = 10;
    private static final Integer ALL_MOTOGP_RIDERS_POINTS = 25;
    private static final Integer ALL_RIDERS_POINTS = 25;
    // Money
    private static final Integer FIRST_MONEY = 100;
    private static final Integer SECOND_MONEY = 50;
    private static final Integer THIRD_MONEY = 50;
    // Repositories
    private final RaceRepository raceRepository;
    private final BetRepository betRepository;
    private final RiderRepository riderRepository;
    private final ClassificationRepository classificationRepository;

    public BetServiceImpl(RaceRepository raceRepository, BetRepository betRepository, RiderRepository riderRepository, ClassificationRepository classificationRepository) {
        this.raceRepository = raceRepository;
        this.betRepository = betRepository;
        this.riderRepository = riderRepository;
        this.classificationRepository = classificationRepository;
    }

    @Override
    public BetDto saveResult(BetDto betDto) {
        Bet existingBet = betRepository.findBetByRaceIdAndTeamId(betDto.getRaceId(), null);
        Bet bet = null;
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
            int totalPoints = calculateTotalPoints(result, uniqueRidersList, motoGpResults, bet);


            ClassifiedTeam classifiedTeam = classifiedTeams.stream().filter(team -> team.getTeam().equals(bet.getTeam())
                    && team.getRace().equals(bet.getRace())).findFirst().get();
            classifiedTeam.setPoints(totalPoints);
        }

        // Order according points
        //TODO: MAKE THIS SORT WORK
        classifiedTeams.sort(Comparator.comparing(ClassifiedTeam::getPoints));
        // Set Position
        calculatePositions(classifiedTeams);
        // Set Money
        calculateMoney(classifiedTeams);

        addToGeneralClassification(classifiedTeams, generalTeams);

        List<ClassifiedTeam> saveClassifiedTeams = new ArrayList<>();
        saveClassifiedTeams.addAll(classifiedTeams);
        saveClassifiedTeams.addAll(generalTeams);
        classificationRepository.saveAll(saveClassifiedTeams);
    }

    private static void addToGeneralClassification(List<ClassifiedTeam> classifiedTeams, List<ClassifiedTeam> generalTeams) {
        for(ClassifiedTeam classifiedTeam : classifiedTeams) {
            // TODO: VIGILAR EL CASO EN EL QUE SE INTRODUZCAN LOS RESULTADOS DE UNA CARRERA Y LUEGO SE CAMBIEN, SE SUMARIAN DOS VECES LOS PUNTOS A LA GENERAL
            ClassifiedTeam generalTeam = generalTeams.stream()
                    .filter(team -> team.getTeam().equals(classifiedTeam.getTeam()))
                    .findFirst().get();
            generalTeam.setPoints(generalTeam.getPoints() + classifiedTeam.getPoints());
            generalTeam.setEarned(generalTeam.getEarned() + classifiedTeam.getEarned());
        }
        calculatePositions(generalTeams);
    }
    private static void calculateMoney(List<ClassifiedTeam> classifiedTeams) {
        int awardedTeams = 0;
        Map<Integer, List<ClassifiedTeam>> podiumMap = classifiedTeams.stream()
                .filter(BetServiceImpl::isaPodium)
                .collect(Collectors.groupingBy(ClassifiedTeam::getPosition));

        List<ClassifiedTeam> firstTeams = podiumMap.get(1);
        if (firstTeams!= null && firstTeams.size() == 1) {
            setEarnedMoney(firstTeams, FIRST_MONEY);
            awardedTeams += 1;
        } else if (firstTeams!= null && firstTeams.size() == 2) {
            Integer givenMoney = FIRST_MONEY + SECOND_MONEY;
            setEarnedMoney(firstTeams, givenMoney);
            awardedTeams += 2;
        } else {
            Integer givenMoney = FIRST_MONEY + SECOND_MONEY + THIRD_MONEY;
            setEarnedMoney(firstTeams, givenMoney);
            awardedTeams += firstTeams.size();
        }

        if (awardedTeams == 1) {
            List<ClassifiedTeam> secondTeams = podiumMap.get(2);
            if (secondTeams!= null && secondTeams.size() == 1) {
                setEarnedMoney(secondTeams, SECOND_MONEY);
                awardedTeams += 1;
            } else {
                Integer givenMoney = SECOND_MONEY + THIRD_MONEY;
                setEarnedMoney(secondTeams, givenMoney);
                awardedTeams += secondTeams.size();
            }
        }

        if (awardedTeams == 2) {
            List<ClassifiedTeam> thirdTeams = podiumMap.get(2);
            setEarnedMoney(thirdTeams, THIRD_MONEY);
        }
    }

    private static void setEarnedMoney(List<ClassifiedTeam> teams, Integer givenMoney) {
        if (teams != null) {
            teams.forEach(classifiedTeam -> classifiedTeam.setEarned(givenMoney / teams.size()));
        }
    }

    private static boolean isaPodium(ClassifiedTeam classifiedTeam) {
        return classifiedTeam.getPosition().equals(1) || classifiedTeam.getPosition().equals(2)
                || classifiedTeam.getPosition().equals(3);
    }
    private static void calculatePositions(List<ClassifiedTeam> classifiedTeams) {
        int position = 1;
        int positionSkipped = 1;
        int pointsLast = 0;
        for(ClassifiedTeam classifiedTeam : classifiedTeams) {
            if (pointsLast == classifiedTeam.getPoints()) {
                classifiedTeam.setPosition(position - positionSkipped);
                ++positionSkipped;
            } else {
                classifiedTeam.setPosition(position);
                pointsLast = classifiedTeam.getPoints();
                positionSkipped = 1;
            }
            ++position;
        }
    }
    private static int calculateTotalPoints(Bet result, List<String> uniqueRidersList, Map<Integer, String> motoGpResults, Bet bet) {
        int totalPoints = 0;
        List<Integer> rightPositions = new ArrayList<>();
        totalPoints = addPointsFromRider(bet.getMoto3(), 7, rightPositions, result.getMoto3(), MOTO3_POINTS, totalPoints);
        totalPoints = addPointsFromRider(bet.getMoto2(), 8, rightPositions, result.getMoto2(), MOTO2_POINTS, totalPoints);
        totalPoints = addPointsFromGpRider(bet.getMotogpFirst(), 1, rightPositions, motoGpResults, uniqueRidersList, MOTOGP_FIRST_POINTS, totalPoints);
        totalPoints = addPointsFromGpRider(bet.getMotogpSecond(), 2, rightPositions, motoGpResults, uniqueRidersList, MOTOGP_SECOND_POINTS, totalPoints);
        totalPoints = addPointsFromGpRider(bet.getMotogpThird(), 3, rightPositions, motoGpResults, uniqueRidersList, MOTOGP_THIRD_POINTS, totalPoints);
        totalPoints = addPointsFromGpRider(bet.getMotogpFourth(), 4, rightPositions, motoGpResults, uniqueRidersList, MOTOGP_FOURTH_POINTS, totalPoints);
        totalPoints = addPointsFromGpRider(bet.getMotogpFifth(), 5, rightPositions, motoGpResults, uniqueRidersList, MOTOGP_FIFTH_POINTS, totalPoints);
        totalPoints = addPointsFromGpRider(bet.getMotogpSixth(), 6, rightPositions, motoGpResults, uniqueRidersList, MOTOGP_SIXTH_POINTS, totalPoints);
        totalPoints = addBonusPointsForAllRiders(rightPositions, totalPoints);
        return totalPoints;
    }

    private static int addBonusPointsForAllRiders(List<Integer> rightPositions, int totalPoints) {
        if (rightPositions.size() >= 6) {
            if (rightPositions.size() == 8) {
                totalPoints += ALL_RIDERS_POINTS + ALL_MOTOGP_RIDERS_POINTS;
            } else if(!rightPositions.contains(7) && !rightPositions.contains(8)) {
                totalPoints += ALL_MOTOGP_RIDERS_POINTS;
            }
        }
        return totalPoints;
    }
    private static int addPointsFromGpRider(String betRider, Integer betPosition, List<Integer> rightPositions,
                                             Map<Integer, String> motoGpResult, List<String> uniqueRidersList,
                                             Integer riderPositionPoints, int totalPoints) {
        if (motoGpResult.containsValue(betRider)) {
            totalPoints += MOTOGP_PRESENT_POINTS;
            rightPositions.add(betPosition);
            if (motoGpResult.get(betPosition).equals(betRider)) {
                totalPoints += riderPositionPoints;
            }
            if (uniqueRidersList.size() > 0 && uniqueRidersList.contains(betRider)) {
                totalPoints += UNIQUE_RIDER_POINTS;
            }
        }
        return totalPoints;
    }
    private static int addPointsFromRider(String betRider, Integer betPosition, List<Integer> rightPositions,
                                           String resultRider, Integer riderPoints, int totalPoints) {
        if (betRider.equals(resultRider)) {
            totalPoints += riderPoints;
            rightPositions.add(betPosition);
        }
        return totalPoints;
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
            Bet bet = betRepository.save(createBet(betDto, user.getTeam(), race.get()));
        } else {
            throw new RuntimeException("Race was not found");
        }

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
}

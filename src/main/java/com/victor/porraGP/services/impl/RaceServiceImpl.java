package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.RaceDto;
import com.victor.porraGP.model.Race;
import com.victor.porraGP.repositories.RaceRepository;
import com.victor.porraGP.services.RaceService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class RaceServiceImpl implements RaceService {

    public static final int SEASON_2023 = 2023;
    private final RaceRepository raceRepository;

    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public RaceDto findRace(Long raceId) {
        RaceDto raceDto = null;
        Race race = raceRepository.findById(raceId).orElse(null);
        if (race != null) {
            raceDto = new RaceDto(race);
        }
        return raceDto;
    }

    @Override
    public RaceDto findNextRace(boolean openedOnly) {
        RaceDto raceDto = null;
        Date now = new Date();
        Race race;
        if (openedOnly) {
            race = raceRepository.findFirstByEndDateAfterAndOpen(now, true);
        } else {
            race = raceRepository.findFirstByEndDateAfter(now);
        }
        if (race != null) {
            raceDto = new RaceDto(race);
        }
        return raceDto;
    }

    @Override
    public List<RaceDto> getAllRaces(boolean general) {
        return StreamSupport.stream(raceRepository.findAll().spliterator(), false)
                .filter(race -> generalRaceFilter(general, race))
                .map(RaceDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RaceDto> getAllRacesBySeason(Integer season, boolean general) {
        return raceRepository.findAllBySeason(season).stream().filter(race -> generalRaceFilter(general, race)).map(RaceDto::new).collect(Collectors.toList());
    }

    @Override
    public boolean closeRace(Long raceId) {
        if (raceId != null && raceId > 0) {
            return raceRepository.updateOpenState(false, raceId) > 0;
        }
        return false;
    }

    @Override
    public boolean openRace(Long raceId) {
        if (raceId != null && raceId > 0) {
            return raceRepository.updateOpenState(true, raceId) > 0;
        }
        return false;
    }

    private static boolean generalRaceFilter(boolean general, Race race) {
        return general || !Objects.equals(race.getName(), "General");
    }
}

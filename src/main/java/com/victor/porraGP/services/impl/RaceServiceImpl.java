package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.RaceDto;
import com.victor.porraGP.model.Race;
import com.victor.porraGP.repositories.RaceRepository;
import com.victor.porraGP.services.RaceService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class RaceServiceImpl implements RaceService {
    private final RaceRepository raceRepository;

    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public RaceDto findNextRace() {
        RaceDto raceDto = null;
        Date now = new Date();
        Race race = raceRepository.findFirstByEndDateAfter(now);
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
    public List<RaceDto> getAllRacesBySeason(Integer season) {
        return raceRepository.findAllBySeason(season).stream().map(RaceDto::new).collect(Collectors.toList());
    }

    private static boolean generalRaceFilter(boolean general, Race race) {
        return general || race.getId() != 0;
    }
}

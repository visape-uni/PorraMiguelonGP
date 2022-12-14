package com.victor.porraGP.services;

import com.victor.porraGP.dto.RaceDto;

import java.util.List;

public interface RaceService {
    RaceDto findNextRace();
    List<RaceDto> getAllRaces(boolean general);
    List<RaceDto> getAllRacesBySeason(Integer season);
}

package com.victor.porraGP.services;

import com.victor.porraGP.dto.BetDto;

import java.util.List;

public interface BetService {
    BetDto saveResult(BetDto betDto);
    BetDto findResult(Long raceId);
    BetDto saveBet(BetDto betDto);
    String validateAndCompleteBet(BetDto betDto, boolean result);
    BetDto findBet(Long raceId) ;
    List<BetDto> findAllBetsByRace(Long raceId);
}

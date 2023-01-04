package com.victor.porraGP.services;

import com.victor.porraGP.dto.BetDto;

public interface BetService {
    BetDto saveResult(BetDto betDto);
    BetDto findResult(Long raceId);
    BetDto saveBet(BetDto betDto);
    String validateAndCompleteBet(BetDto betDto);
    BetDto findBet(Long raceId) ;
}

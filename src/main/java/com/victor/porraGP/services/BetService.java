package com.victor.porraGP.services;

import com.victor.porraGP.dto.BetDto;

public interface BetService {
    BetDto saveBet(BetDto betDto);
    String validateAndCompleteBet(BetDto betDto);
}

package com.victor.porraGP.repositories;

import com.victor.porraGP.model.Bet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends CrudRepository<Bet, Long> {
    Bet findBetByRaceIdAndTeamId(Long raceId, Long teamId);
    List<Bet> findBetsByRaceId(Long raceId);
}

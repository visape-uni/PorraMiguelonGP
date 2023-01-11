package com.victor.porraGP.repositories;

import com.victor.porraGP.model.ClassifiedTeam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassificationRepository extends CrudRepository<ClassifiedTeam, Long> {
    List<ClassifiedTeam> findClassificationsByRaceIdIsOrderByPositionAsc(Long raceId);
    List<ClassifiedTeam> findClassificationsByRaceId(Long raceId);
    @Query("select t from ClassifiedTeam t where t.race.id <> ?1 and (t.points > 0 or t.earned > 0)")
    List<ClassifiedTeam> findClassificationsByRaceIdIsNotAndHasPointsOrEarned(Long raceId);
    long countAllByRaceId(Long raceId);
}

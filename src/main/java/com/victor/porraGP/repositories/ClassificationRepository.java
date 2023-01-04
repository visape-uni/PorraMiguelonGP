package com.victor.porraGP.repositories;

import com.victor.porraGP.model.ClassifiedTeam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassificationRepository extends CrudRepository<ClassifiedTeam, Long> {
    List<ClassifiedTeam> findClassificationsByRaceIdIsOrderByPositionAsc(Long raceId);
    List<ClassifiedTeam> findClassificationsByRaceId(Long raceId);
    long countAllByRaceId(Long raceId);
}

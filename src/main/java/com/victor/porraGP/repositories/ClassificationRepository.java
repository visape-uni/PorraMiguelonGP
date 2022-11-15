package com.victor.porraGP.repositories;

import com.victor.porraGP.model.ClassifiedTeam;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClassificationRepository extends CrudRepository<ClassifiedTeam, Long> {
    List<ClassifiedTeam> findClassificationsByRaceIdIsOrderByPositionAsc(Long raceId);
}

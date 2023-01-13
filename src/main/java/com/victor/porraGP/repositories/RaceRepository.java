package com.victor.porraGP.repositories;

import com.victor.porraGP.model.Race;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;

@Repository
public interface RaceRepository extends CrudRepository<Race, Long> {
    Race findFirstByEndDateAfter(Date date);
    Collection<Race> findAllBySeason(Integer season);
}

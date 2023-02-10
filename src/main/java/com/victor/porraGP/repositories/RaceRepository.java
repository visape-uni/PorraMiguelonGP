package com.victor.porraGP.repositories;

import com.victor.porraGP.model.Race;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;

@Repository
public interface RaceRepository extends CrudRepository<Race, Long> {
    Race findFirstByEndDateAfterAndOpen(Date date, boolean open);
    Race findFirstByEndDateAfter(Date date);
    Collection<Race> findAllBySeason(Integer season);
    @Modifying
    @Transactional
    @Query("UPDATE Race SET open = :open WHERE id = :id")
    int updateOpenState(@Param("open") boolean open, @Param("id") Long raceId);
}

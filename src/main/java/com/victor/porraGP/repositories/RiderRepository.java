package com.victor.porraGP.repositories;

import com.victor.porraGP.model.Rider;
import com.victor.porraGP.model.RiderId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends CrudRepository<Rider, RiderId> {
}

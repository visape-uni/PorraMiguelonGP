package com.victor.porraGP.repositories;

import com.victor.porraGP.model.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {
    Configuration findConfigurationByClave(String key);
}

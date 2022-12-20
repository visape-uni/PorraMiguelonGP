package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.RiderDto;
import com.victor.porraGP.repositories.RiderRepository;
import com.victor.porraGP.services.RiderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RiderServiceImpl implements RiderService {
    private final RiderRepository riderRepository;

    public RiderServiceImpl(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    @Override
    public Map<String, List<RiderDto>> getAllRiders() {
        return StreamSupport.stream(riderRepository.findAll().spliterator(), false)
                .map(RiderDto::new)
                .collect(Collectors.groupingBy(RiderDto::getCategory));
    }
}

package com.victor.porraGP.services;

import com.victor.porraGP.dto.RiderDto;

import java.util.List;
import java.util.Map;

public interface RiderService {
    Map<String, List<RiderDto>> getAllRiders();
}

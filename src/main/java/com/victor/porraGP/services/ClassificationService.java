package com.victor.porraGP.services;

import com.victor.porraGP.dto.ClassificationDto;

public interface ClassificationService {
    ClassificationDto findClassificationByRace(Long id);
}

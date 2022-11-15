package com.victor.porraGP.services;

import com.victor.porraGP.dto.ClassificationDto;

public interface ClassificationService {
    /*
    id should be null, if want to retrieve the General classification
     */
    ClassificationDto findClassificationByRace(Long id);
}

package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.ClassificationDto;
import com.victor.porraGP.model.ClassifiedTeam;
import com.victor.porraGP.repositories.ClassificationRepository;
import com.victor.porraGP.services.ClassificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassificationServiceImpl implements ClassificationService {

    private final ClassificationRepository classificationRepository;

    public ClassificationServiceImpl(ClassificationRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
    }

    @Override
    public ClassificationDto findClassificationByRace(Long id) {
        List<ClassifiedTeam> classificationList = classificationRepository.findClassificationsByRaceIdIsOrderByPositionAsc(id);
        if (classificationList != null && !classificationList.isEmpty()) {
            ClassifiedTeam firstClassified = classificationList.stream().filter(classifiedTeam -> classifiedTeam.getPosition() != 0).findFirst().orElse(classificationList.get(0));
            return new ClassificationDto(classificationList, firstClassified);
        }
        return null;
    }

}

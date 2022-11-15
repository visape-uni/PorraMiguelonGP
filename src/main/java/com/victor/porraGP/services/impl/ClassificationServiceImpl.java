package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.ClassificationDto;
import com.victor.porraGP.model.ClassifiedTeam;
import com.victor.porraGP.repositories.ClassificationRepository;
import com.victor.porraGP.repositories.RaceRepository;
import com.victor.porraGP.services.ClassificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassificationServiceImpl implements ClassificationService {

    private final ClassificationRepository classificationRepository;

    public ClassificationServiceImpl(ClassificationRepository classificationRepository, RaceRepository raceRepository) {
        this.classificationRepository = classificationRepository;
    }

    @Override
    public ClassificationDto findClassificationByRace(Long id) {
        List<ClassifiedTeam> classifiedTeamList = classificationRepository.findClassificationsByRaceIdIsOrderByPositionAsc(id);
        if (classifiedTeamList != null && !classifiedTeamList.isEmpty()) {
            ClassifiedTeam firstClassified = classifiedTeamList.get(0);
            return new ClassificationDto(classifiedTeamList, firstClassified);
        }
        return null;
    }
}

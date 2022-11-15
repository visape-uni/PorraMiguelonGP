package com.victor.porraGP.dto;

import com.victor.porraGP.model.ClassifiedTeam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClassificationDto {

    private String raceName;
    private List<ClassifiedTeamDto> classifiedTeams = new ArrayList<>();

    private static final String GENERAL_CLASSIFICATION = "General";


    public ClassificationDto(List<ClassifiedTeam> classifiedTeams, ClassifiedTeam firstClassified) {
        for(ClassifiedTeam classifiedTeam : classifiedTeams) {
            ClassifiedTeamDto classifiedTeamDto = new ClassifiedTeamDto();
            classifiedTeamDto.position = classifiedTeam.getPosition();
            classifiedTeamDto.teamName = classifiedTeam.getTeam().getName();
            classifiedTeamDto.points = classifiedTeam.getPoints();
            classifiedTeamDto.pointsDif = calculatePointsDif(classifiedTeam, firstClassified);
            this.classifiedTeams.add(classifiedTeamDto);
        }
        if (firstClassified.getRace() != null) {
            this.raceName = firstClassified.getRace().getName();
        } else {
            this.raceName = GENERAL_CLASSIFICATION;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    private static class ClassifiedTeamDto {
        private Long position;
        private String teamName;
        private Integer points;
        private Integer pointsDif;
    }

    private Integer calculatePointsDif(ClassifiedTeam actualTeam, ClassifiedTeam firstClassified) {
        if (actualTeam.equals(firstClassified)) {
            return 0;
        } else {
            return firstClassified.getPoints() - actualTeam.getPoints();
        }
    }
}

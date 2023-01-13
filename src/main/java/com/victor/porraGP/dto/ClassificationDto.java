package com.victor.porraGP.dto;

import com.victor.porraGP.model.ClassifiedTeam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ClassificationDto {

    private String raceName;
    private List<ClassifiedTeamDto> classifiedTeams = new ArrayList<>();

    private static final String GENERAL_CLASSIFICATION = "General";


    public ClassificationDto(List<ClassifiedTeam> classifiedTeams, ClassifiedTeam firstClassified) {
        List<ClassifiedTeamDto> noClassified = new ArrayList<>();
        for(ClassifiedTeam classifiedTeam : classifiedTeams) {
            ClassifiedTeamDto classifiedTeamDto = new ClassifiedTeamDto();
            classifiedTeamDto.position = classifiedTeam.getPosition();
            classifiedTeamDto.teamName = classifiedTeam.getTeam().getName();
            classifiedTeamDto.entryPoints = classifiedTeam.getEntryPoints();
            classifiedTeamDto.positionPoints = classifiedTeam.getPositionPoints();
            classifiedTeamDto.bonificationGpPoints = classifiedTeam.getBonificationGpPoints();
            classifiedTeamDto.motoTwoAndThreePoints = classifiedTeam.getMotoTwoAndThreePoints();
            classifiedTeamDto.totalGpPoints = classifiedTeam.getTotalGpPoints();
            classifiedTeamDto.totalPoints = classifiedTeam.getTotalPoints();
            classifiedTeamDto.pointsDif = calculatePointsDif(classifiedTeam, firstClassified);
            classifiedTeamDto.earned = classifiedTeam.getEarned();
            if (classifiedTeam.getPosition() != 0) {
                this.classifiedTeams.add(classifiedTeamDto);
            } else {
                noClassified.add(classifiedTeamDto);
            }
        }
        this.classifiedTeams.addAll(noClassified);
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
        private Integer position;
        private String teamName;
        private Integer entryPoints;
        private Integer positionPoints;
        private Integer bonificationGpPoints;
        private Integer totalGpPoints;
        private Integer motoTwoAndThreePoints;
        private Integer totalPoints;
        private Integer pointsDif;
        private Double earned;
    }

    private Integer calculatePointsDif(ClassifiedTeam actualTeam, ClassifiedTeam firstClassified) {
        if (Objects.isNull(firstClassified) || actualTeam.equals(firstClassified)) {
            return 0;
        } else {
            return firstClassified.getTotalPoints() - actualTeam.getTotalPoints();
        }
    }
}

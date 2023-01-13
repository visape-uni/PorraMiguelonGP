package com.victor.porraGP.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "classification", uniqueConstraints = {@UniqueConstraint(name = "uniqueClassifiedTeamRace", columnNames = {"team_id", "race_id"})})
public class ClassifiedTeam extends BaseEntity {
    private Integer position;
    @Column(name = "entry_points")
    private Integer entryPoints;
    @Column(name = "position_points")
    private Integer positionPoints;
    @Column(name = "bonification_gp_points")
    private Integer bonificationGpPoints;
    @Column(name = "total_gp_points")
    private Integer totalGpPoints;
    @Column(name = "moto_two_and_three_points")
    private Integer motoTwoAndThreePoints;
    @Column(name = "total_points")
    private Integer totalPoints;
    @Column(name = "earned")
    private Double earned;
    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
    @ManyToOne
    @JoinColumn(name = "race_id", referencedColumnName = "id")
    private Race race;
}

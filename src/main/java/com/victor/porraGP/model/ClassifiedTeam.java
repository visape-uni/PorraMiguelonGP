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
    @Column(name = "points")
    private Integer points;
    @Column(name = "earned")
    private Integer earned;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
    @ManyToOne
    @JoinColumn(name = "race_id", referencedColumnName = "id")
    private Race race;
}

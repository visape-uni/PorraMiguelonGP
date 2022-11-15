package com.victor.porraGP.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(ClassifiedTeamId.class)
@Table(name = "classified_teams")
public class ClassifiedTeam implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long position;
    @Column(name = "points")
    private Integer points;
    @Column(name = "earned")
    private Integer earned;

    @ManyToOne
    @Id
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
    @ManyToOne
    @Id
    @JoinColumn(name = "race_id", referencedColumnName = "id")
    private Race race;
}

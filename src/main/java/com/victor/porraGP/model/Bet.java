package com.victor.porraGP.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bets", schema = "public", uniqueConstraints = {@UniqueConstraint(name = "uniqueBetTeamRace", columnNames = {"team_id", "race_id"})})
public class Bet extends BaseEntity {
    @Column(name = "moto3")
    private String moto3;
    @Column(name = "moto2")
    private String moto2;
    @Column(name = "motogp_1")
    private String motogpFirst;
    @Column(name = "motogp_2")
    private String motogpSecond;
    @Column(name = "motogp_3")
    private String motogpThird;
    @Column(name = "motogp_4")
    private String motogpFourth;
    @Column(name = "motogp_5")
    private String motogpFifth;
    @Column(name = "motogp_6")
    private String motogpSixth;
    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
    @ManyToOne
    @JoinColumn(name = "race_id", referencedColumnName = "id")
    private Race race;
    @Column(name = "result")
    private Boolean result;
}

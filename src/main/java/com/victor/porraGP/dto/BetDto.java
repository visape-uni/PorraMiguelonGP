package com.victor.porraGP.dto;

import com.victor.porraGP.model.Bet;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BetDto {
    private Long raceId;
    @NotBlank(message = "error.pilotBetEmpty")
    private String moto3;
    @NotBlank(message = "error.pilotBetEmpty")
    private String moto2;
    @NotBlank(message = "error.pilotBetEmpty")
    private String motogpFirst;
    @NotBlank(message = "error.pilotBetEmpty")
    private String motogpSecond;
    @NotBlank(message = "error.pilotBetEmpty")
    private String motogpThird;
    @NotBlank(message = "error.pilotBetEmpty")
    private String motogpFourth;
    @NotBlank(message = "error.pilotBetEmpty")
    private String motogpFifth;
    @NotBlank(message = "error.pilotBetEmpty")
    private String motogpSixth;
    private String teamName;

    public BetDto(Long raceId, String moto3, String moto2, String motogpFirst, String motogpSecond, String motogpThird,
                  String motogpFourth, String motogpFifth, String motogpSixth) {
        this.raceId = raceId;
        this.moto3 = moto3;
        this.moto2 = moto2;
        this.motogpFirst = motogpFirst;
        this.motogpSecond = motogpSecond;
        this.motogpThird = motogpThird;
        this.motogpFourth = motogpFourth;
        this.motogpFifth = motogpFifth;
        this.motogpSixth = motogpSixth;
    }
    public BetDto(Bet bet) {
        this.raceId = bet.getRace().getId();
        this.moto3 = bet.getMoto3();
        this.moto2 = bet.getMoto2();
        this.motogpFirst = bet.getMotogpFirst();
        this.motogpSecond = bet.getMotogpSecond();
        this.motogpThird = bet.getMotogpThird();
        this.motogpFourth = bet.getMotogpFourth();
        this.motogpFifth = bet.getMotogpFifth();
        this.motogpSixth = bet.getMotogpSixth();
        this.teamName = bet.getTeam().getName();
    }
}

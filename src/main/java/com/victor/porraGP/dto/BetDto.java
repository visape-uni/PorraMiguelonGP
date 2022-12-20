package com.victor.porraGP.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BetDto {
    private RaceDto race;
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
    private String motogpForth;
    @NotBlank(message = "error.pilotBetEmpty")
    private String motogpFifth;
    @NotBlank(message = "error.pilotBetEmpty")
    private String motogpSixth;
}

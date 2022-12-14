package com.victor.porraGP.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BetDto {
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

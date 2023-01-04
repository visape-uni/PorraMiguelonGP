package com.victor.porraGP.dto;

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
}

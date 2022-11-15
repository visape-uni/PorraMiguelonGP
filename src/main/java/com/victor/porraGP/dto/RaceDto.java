package com.victor.porraGP.dto;

import com.victor.porraGP.model.Race;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RaceDto {
    private Long id;
    private String name;
    private String country;
    private Date startDate;
    private Date endDate;

    public RaceDto(Race race) {
        this.id = race.getId();
        this.name = race.getName();
        this.country = race.getCountry();
        this.startDate = race.getStartDate();
        this.endDate = race.getEndDate();
    }

}

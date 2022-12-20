package com.victor.porraGP.dto;

import com.victor.porraGP.model.Race;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RaceDto {
    private Long id;
    private String name;
    private String country;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    private Date endDate;

    public RaceDto(Race race) {
        this.id = race.getId();
        this.name = race.getName();
        this.country = race.getCountry();
        this.startDate = race.getStartDate();
        this.endDate = race.getEndDate();
    }

}

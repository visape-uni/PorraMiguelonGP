package com.victor.porraGP.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "races")
public class Race extends BaseEntity {

    @Column(name = "country")
    private String country;
    @Column(name = "name")
    private String name;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "season")
    private Integer season;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "race")
    private List<ClassifiedTeam> classifications = new ArrayList<>();
}

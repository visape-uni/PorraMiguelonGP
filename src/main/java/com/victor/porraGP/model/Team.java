package com.victor.porraGP.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "teams")
public class Team extends BaseEntity {

    @Column(name = "name")
    private String name;
    @Column(name = "total_earned")
    private Integer totalEarned;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
    private List<ClassifiedTeam> classifications = new ArrayList<>();
}

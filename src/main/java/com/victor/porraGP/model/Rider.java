package com.victor.porraGP.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "riders")
@IdClass(RiderId.class)
public class Rider implements Serializable {
    @Id
    private Long dorsal;
    @Id
    @Column(name = "category")
    private String category;
    @Column(name = "name")
    private String name;
    @Column(name = "team")
    private String team;
}

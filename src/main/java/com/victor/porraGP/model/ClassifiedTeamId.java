package com.victor.porraGP.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ClassifiedTeamId implements Serializable {
    private Long team;
    private Long race;
}

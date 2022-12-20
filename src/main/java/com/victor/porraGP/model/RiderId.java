package com.victor.porraGP.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RiderId implements Serializable {
    private Long dorsal;
    private String category;
}

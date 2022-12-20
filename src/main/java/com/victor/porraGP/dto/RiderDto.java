package com.victor.porraGP.dto;

import com.victor.porraGP.model.Rider;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RiderDto {
    private Long dorsal;
    private String name;
    private String team;
    private String category;

    public RiderDto(Rider rider) {
        this.dorsal = rider.getDorsal();
        this.name = rider.getName();
        this.team = rider.getTeam();
        this.category = rider.getCategory();
    }
}

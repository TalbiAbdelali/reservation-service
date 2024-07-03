package com.test.reservations_service.dto;

import com.test.reservations_service.model.Restaurant;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TableAvailabilityDTO {
    private Long id;
    private String date;
    private int availableTables;
    private Restaurant restaurant;
}

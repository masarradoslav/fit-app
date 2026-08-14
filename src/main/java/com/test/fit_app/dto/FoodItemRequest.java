package com.test.fit_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemRequest {

    @NotBlank
    private String name;

    @PositiveOrZero
    private double proteinPer100g;

    @PositiveOrZero
    private double carbsPer100g;

    @PositiveOrZero
    private double fatPer100g;

    @PositiveOrZero
    private double caloriesPer100g;
}

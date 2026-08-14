package com.test.fit_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemResponse {

    private int id;
    private String name;
    private double proteinPer100g;
    private double carbsPer100g;
    private double fatPer100g;
    private double caloriesPer100g;
}

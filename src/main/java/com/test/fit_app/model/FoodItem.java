package com.test.fit_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single food item (e.g. "Chicken Breast") in the food catalog, with
 * macros expressed per 100g.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private double proteinPer100g;

    private double carbsPer100g;

    private double fatPer100g;

    private double caloriesPer100g;
}

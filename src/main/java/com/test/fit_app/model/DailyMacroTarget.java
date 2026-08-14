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
 * The user's daily macro goals, used to compare against actual intake
 * logged via {@link MealEntry}. There is only ever one row.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyMacroTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double targetProtein;

    private double targetCarbs;

    private double targetFat;

    private double targetCalories;
}

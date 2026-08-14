package com.test.fit_app.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyNutritionSummaryResponse {

    private LocalDate date;
    private double totalProtein;
    private double totalCarbs;
    private double totalFat;
    private double totalCalories;

    // Null when no target has been set yet.
    private DailyMacroTargetResponse target;
    private Double proteinDelta;
    private Double carbsDelta;
    private Double fatDelta;
    private Double caloriesDelta;
}

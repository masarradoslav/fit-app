package com.test.fit_app.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyMacroTargetRequest {

    @PositiveOrZero
    private double targetProtein;

    @PositiveOrZero
    private double targetCarbs;

    @PositiveOrZero
    private double targetFat;

    @PositiveOrZero
    private double targetCalories;
}

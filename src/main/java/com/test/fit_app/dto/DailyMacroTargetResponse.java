package com.test.fit_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyMacroTargetResponse {

    private int id;
    private double targetProtein;
    private double targetCarbs;
    private double targetFat;
    private double targetCalories;
}

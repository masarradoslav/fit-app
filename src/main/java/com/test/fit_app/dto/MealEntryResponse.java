package com.test.fit_app.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealEntryResponse {

    private int id;
    private int foodItemId;
    private String foodItemName;
    private double amountGrams;
    private LocalDateTime loggedAt;
    private double protein;
    private double carbs;
    private double fat;
    private double calories;
}

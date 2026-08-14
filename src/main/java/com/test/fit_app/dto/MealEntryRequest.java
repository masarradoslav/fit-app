package com.test.fit_app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealEntryRequest {

    @NotNull
    private Integer foodItemId;

    @Positive
    private double amountGrams;

    // Defaults to now if omitted.
    private LocalDateTime loggedAt;
}

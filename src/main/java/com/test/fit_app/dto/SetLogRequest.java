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
public class SetLogRequest {

    @PositiveOrZero
    private int reps;

    @PositiveOrZero
    private double weightKg;
}

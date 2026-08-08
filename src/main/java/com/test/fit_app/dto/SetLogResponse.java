package com.test.fit_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetLogResponse {

    private int id;
    private int setIndex;
    private int reps;
    private double weightKg;
}

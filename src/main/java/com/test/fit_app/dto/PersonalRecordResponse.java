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
public class PersonalRecordResponse {

    private int id;
    private int exerciseId;
    private String exerciseName;
    private Integer userId;
    private LocalDate date;
    private double weightKg;
}

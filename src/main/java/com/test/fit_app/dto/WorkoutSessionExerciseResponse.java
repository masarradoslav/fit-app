package com.test.fit_app.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSessionExerciseResponse {

    private int id;
    private int exerciseId;
    private String exerciseName;
    private int position;
    private List<SetLogResponse> sets;
}

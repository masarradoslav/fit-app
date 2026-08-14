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
public class ExerciseComparisonResponse {

    private int workoutSessionExerciseId;
    private int exerciseId;
    private String exerciseName;
    private boolean hasPreviousSession;
    private List<SetComparisonResponse> sets;
}

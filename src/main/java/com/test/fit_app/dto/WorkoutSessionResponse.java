package com.test.fit_app.dto;

import com.test.fit_app.model.enums.Musclegroups;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSessionResponse {

    private int id;
    private String name;
    private LocalDateTime date;

    // Derived from the muscleGroup of every exercise added to this session,
    // not stored, so it can never drift out of sync with what was logged.
    private Set<Musclegroups> muscleGroupsHit;

    private List<WorkoutSessionExerciseResponse> exercises;
}

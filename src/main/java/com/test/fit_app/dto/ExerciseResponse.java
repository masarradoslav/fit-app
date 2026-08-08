package com.test.fit_app.dto;

import com.test.fit_app.model.enums.Muscle;
import com.test.fit_app.model.enums.Musclegroups;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {

    private int id;
    private String name;
    private Musclegroups muscleGroup;
    private Muscle muscle;
}

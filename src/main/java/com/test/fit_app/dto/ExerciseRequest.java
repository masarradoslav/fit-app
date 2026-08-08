package com.test.fit_app.dto;

import com.test.fit_app.model.enums.Muscle;
import com.test.fit_app.model.enums.Musclegroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRequest {

    @NotBlank
    private String name;

    @NotNull
    private Musclegroups muscleGroup;

    @NotNull
    private Muscle muscle;
}

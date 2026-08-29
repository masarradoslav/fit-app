package com.test.fit_app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecordRequest {

    @NotNull
    private Integer exerciseId;

    // Not required yet: there is no auth/User entity to populate it from.
    private Integer userId;

    @NotNull
    private LocalDate date;

    @Positive
    private double weightKg;
}

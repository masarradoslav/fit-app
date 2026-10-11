package com.test.fit_app.model;

import com.test.fit_app.model.enums.Muscle;
import com.test.fit_app.model.enums.Musclegroups;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single exercise (e.g. "Bench Press") that can be added to a
 * {@code TrainingDay}.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    // stored as STRING rather than default ORDINAL so reordering/adding enum fields doesnt corrupt existing rows
    @Enumerated(EnumType.STRING)
    private Musclegroups muscleGroup;

    @Enumerated(EnumType.STRING)
    private Muscle muscle;
}

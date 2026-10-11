package com.test.fit_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Links an {@link Exercise} to a {@link WorkoutSession} it was performed in
 * (e.g. "Bench Press" added to today's session), independently of whether
 * any {@link SetLog}s have been logged for it yet.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSessionExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_session_id")
    private WorkoutSession workoutSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    // position within session
    private int position;
}

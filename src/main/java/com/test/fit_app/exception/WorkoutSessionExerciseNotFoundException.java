package com.test.fit_app.exception;

public class WorkoutSessionExerciseNotFoundException extends RuntimeException {

    public WorkoutSessionExerciseNotFoundException(int id, int workoutSessionId) {
        super("WorkoutSessionExercise with id " + id + " not found in WorkoutSession " + workoutSessionId);
    }
}

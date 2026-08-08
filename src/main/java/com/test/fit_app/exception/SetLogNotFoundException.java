package com.test.fit_app.exception;

public class SetLogNotFoundException extends RuntimeException {

    public SetLogNotFoundException(int id, int workoutSessionExerciseId) {
        super("SetLog with id " + id + " not found in WorkoutSessionExercise " + workoutSessionExerciseId);
    }
}

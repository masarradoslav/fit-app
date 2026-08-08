package com.test.fit_app.exception;

public class WorkoutSessionNotFoundException extends RuntimeException {

    public WorkoutSessionNotFoundException(int id) {
        super("WorkoutSession with id " + id + " not found");
    }
}

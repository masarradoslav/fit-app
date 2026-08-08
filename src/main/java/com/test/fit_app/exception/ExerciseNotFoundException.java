package com.test.fit_app.exception;

public class ExerciseNotFoundException extends RuntimeException {

    public ExerciseNotFoundException(int id) {
        super("Exercise with id " + id + " not found");
    }
}

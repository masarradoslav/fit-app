package com.test.fit_app.exception;

public class MealEntryNotFoundException extends RuntimeException {

    public MealEntryNotFoundException(int id) {
        super("Meal entry with id " + id + " not found");
    }
}

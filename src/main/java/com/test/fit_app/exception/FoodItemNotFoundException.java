package com.test.fit_app.exception;

public class FoodItemNotFoundException extends RuntimeException {

    public FoodItemNotFoundException(int id) {
        super("Food item with id " + id + " not found");
    }
}

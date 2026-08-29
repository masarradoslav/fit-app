package com.test.fit_app.exception;

public class PersonalRecordNotFoundException extends RuntimeException {

    public PersonalRecordNotFoundException(int id) {
        super("PersonalRecord with id " + id + " not found");
    }
}

package com.test.fit_app.exception;

public class DailyMacroTargetNotFoundException extends RuntimeException {

    public DailyMacroTargetNotFoundException() {
        super("Daily macro target has not been set");
    }
}

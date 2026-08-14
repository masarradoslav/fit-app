package com.test.fit_app.dto;

public enum SetComparisonStatus {
    // Same set position exists in both sessions.
    MATCHED,
    // This session has a set at this position that the previous session didn't.
    EXTRA_SET,
    // The previous session had a set at this position that this session didn't.
    MISSING_SET
}

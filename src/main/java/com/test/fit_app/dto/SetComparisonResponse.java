package com.test.fit_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetComparisonResponse {

    private int setNumber;
    private SetLogResponse current;
    private SetLogResponse previous;
    private Double weightDeltaKg;
    private Integer repsDelta;
    private SetComparisonStatus status;
}

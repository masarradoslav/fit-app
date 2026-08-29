package com.test.fit_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A personal best weight lifted for a given {@link Exercise} on a given date.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    // Plain id rather than a relation for now: there is no User entity yet.
    // Will become a @ManyToOne once auth is added.
    private Integer userId;

    private LocalDate date;

    private double weightKg;
}

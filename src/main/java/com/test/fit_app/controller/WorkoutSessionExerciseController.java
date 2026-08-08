package com.test.fit_app.controller;

import com.test.fit_app.dto.WorkoutSessionExerciseRequest;
import com.test.fit_app.dto.WorkoutSessionExerciseResponse;
import com.test.fit_app.service.WorkoutSessionExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workout-sessions/{sessionId}/exercises")
@RequiredArgsConstructor
public class WorkoutSessionExerciseController {

    private final WorkoutSessionExerciseService workoutSessionExerciseService;

    @PostMapping
    public ResponseEntity<WorkoutSessionExerciseResponse> addExercise(
            @PathVariable int sessionId, @Valid @RequestBody WorkoutSessionExerciseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workoutSessionExerciseService.addExercise(sessionId, request));
    }

    @DeleteMapping("/{workoutSessionExerciseId}")
    public ResponseEntity<Void> removeExercise(
            @PathVariable int sessionId, @PathVariable int workoutSessionExerciseId) {
        workoutSessionExerciseService.removeExercise(sessionId, workoutSessionExerciseId);
        return ResponseEntity.noContent().build();
    }
}

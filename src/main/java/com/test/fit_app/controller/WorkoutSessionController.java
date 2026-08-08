package com.test.fit_app.controller;

import com.test.fit_app.dto.WorkoutSessionRequest;
import com.test.fit_app.dto.WorkoutSessionResponse;
import com.test.fit_app.service.WorkoutSessionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workout-sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> create(@Valid @RequestBody WorkoutSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutSessionService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(workoutSessionService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<WorkoutSessionResponse>> findAll() {
        return ResponseEntity.ok(workoutSessionService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponse> update(
            @PathVariable int id, @Valid @RequestBody WorkoutSessionRequest request) {
        return ResponseEntity.ok(workoutSessionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        workoutSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

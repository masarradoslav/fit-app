package com.test.fit_app.controller;

import com.test.fit_app.dto.ExerciseRequest;
import com.test.fit_app.dto.ExerciseResponse;
import com.test.fit_app.service.ExerciseService;
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
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(exerciseService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> findAll() {
        return ResponseEntity.ok(exerciseService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(@PathVariable int id, @Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

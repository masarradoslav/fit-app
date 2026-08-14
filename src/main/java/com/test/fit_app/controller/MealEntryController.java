package com.test.fit_app.controller;

import com.test.fit_app.dto.DailyNutritionSummaryResponse;
import com.test.fit_app.dto.MealEntryRequest;
import com.test.fit_app.dto.MealEntryResponse;
import com.test.fit_app.service.MealEntryService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meal-entries")
@RequiredArgsConstructor
public class MealEntryController {

    private final MealEntryService mealEntryService;

    @PostMapping
    public ResponseEntity<MealEntryResponse> create(@Valid @RequestBody MealEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mealEntryService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealEntryResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(mealEntryService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<MealEntryResponse>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealEntryService.findAll(date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealEntryResponse> update(@PathVariable int id, @Valid @RequestBody MealEntryRequest request) {
        return ResponseEntity.ok(mealEntryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        mealEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<DailyNutritionSummaryResponse> getDailySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealEntryService.getDailySummary(date));
    }
}

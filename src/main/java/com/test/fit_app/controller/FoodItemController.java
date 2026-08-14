package com.test.fit_app.controller;

import com.test.fit_app.dto.FoodItemRequest;
import com.test.fit_app.dto.FoodItemResponse;
import com.test.fit_app.service.FoodItemService;
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
@RequestMapping("/api/food-items")
@RequiredArgsConstructor
public class FoodItemController {

    private final FoodItemService foodItemService;

    @PostMapping
    public ResponseEntity<FoodItemResponse> create(@Valid @RequestBody FoodItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodItemService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItemResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(foodItemService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<FoodItemResponse>> findAll() {
        return ResponseEntity.ok(foodItemService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemResponse> update(@PathVariable int id, @Valid @RequestBody FoodItemRequest request) {
        return ResponseEntity.ok(foodItemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        foodItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

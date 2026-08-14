package com.test.fit_app.service;

import com.test.fit_app.dto.FoodItemRequest;
import com.test.fit_app.dto.FoodItemResponse;
import com.test.fit_app.exception.FoodItemNotFoundException;
import com.test.fit_app.model.FoodItem;
import com.test.fit_app.repository.FoodItemRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemResponse create(FoodItemRequest request) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(request.getName());
        foodItem.setProteinPer100g(request.getProteinPer100g());
        foodItem.setCarbsPer100g(request.getCarbsPer100g());
        foodItem.setFatPer100g(request.getFatPer100g());
        foodItem.setCaloriesPer100g(request.getCaloriesPer100g());
        return toResponse(foodItemRepository.save(foodItem));
    }

    public FoodItemResponse findById(int id) {
        return toResponse(getFoodItemOrThrow(id));
    }

    public List<FoodItemResponse> findAll() {
        return foodItemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FoodItemResponse update(int id, FoodItemRequest request) {
        FoodItem foodItem = getFoodItemOrThrow(id);
        foodItem.setName(request.getName());
        foodItem.setProteinPer100g(request.getProteinPer100g());
        foodItem.setCarbsPer100g(request.getCarbsPer100g());
        foodItem.setFatPer100g(request.getFatPer100g());
        foodItem.setCaloriesPer100g(request.getCaloriesPer100g());
        return toResponse(foodItemRepository.save(foodItem));
    }

    public void delete(int id) {
        FoodItem foodItem = getFoodItemOrThrow(id);
        foodItemRepository.delete(foodItem);
    }

    private FoodItem getFoodItemOrThrow(int id) {
        Optional<FoodItem> foodItem = foodItemRepository.findById(id);
        return foodItem.orElseThrow(() -> new FoodItemNotFoundException(id));
    }

    private FoodItemResponse toResponse(FoodItem foodItem) {
        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getProteinPer100g(),
                foodItem.getCarbsPer100g(),
                foodItem.getFatPer100g(),
                foodItem.getCaloriesPer100g());
    }
}

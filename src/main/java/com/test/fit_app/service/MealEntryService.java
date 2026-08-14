package com.test.fit_app.service;

import com.test.fit_app.dto.DailyMacroTargetResponse;
import com.test.fit_app.dto.DailyNutritionSummaryResponse;
import com.test.fit_app.dto.MealEntryRequest;
import com.test.fit_app.dto.MealEntryResponse;
import com.test.fit_app.exception.FoodItemNotFoundException;
import com.test.fit_app.exception.MealEntryNotFoundException;
import com.test.fit_app.model.DailyMacroTarget;
import com.test.fit_app.model.FoodItem;
import com.test.fit_app.model.MealEntry;
import com.test.fit_app.repository.DailyMacroTargetRepository;
import com.test.fit_app.repository.FoodItemRepository;
import com.test.fit_app.repository.MealEntryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealEntryService {

    private final MealEntryRepository mealEntryRepository;
    private final FoodItemRepository foodItemRepository;
    private final DailyMacroTargetRepository dailyMacroTargetRepository;

    public MealEntryResponse create(MealEntryRequest request) {
        FoodItem foodItem = foodItemRepository
                .findById(request.getFoodItemId())
                .orElseThrow(() -> new FoodItemNotFoundException(request.getFoodItemId()));

        MealEntry entry = new MealEntry();
        entry.setFoodItem(foodItem);
        entry.setAmountGrams(request.getAmountGrams());
        entry.setLoggedAt(request.getLoggedAt() != null ? request.getLoggedAt() : LocalDateTime.now());
        return toResponse(mealEntryRepository.save(entry));
    }

    public MealEntryResponse findById(int id) {
        return toResponse(getMealEntryOrThrow(id));
    }

    public List<MealEntryResponse> findAll(LocalDate date) {
        List<MealEntry> entries = date != null ? findByDate(date) : mealEntryRepository.findAll();
        return entries.stream().map(this::toResponse).toList();
    }

    public MealEntryResponse update(int id, MealEntryRequest request) {
        MealEntry entry = getMealEntryOrThrow(id);
        FoodItem foodItem = foodItemRepository
                .findById(request.getFoodItemId())
                .orElseThrow(() -> new FoodItemNotFoundException(request.getFoodItemId()));

        entry.setFoodItem(foodItem);
        entry.setAmountGrams(request.getAmountGrams());
        entry.setLoggedAt(request.getLoggedAt() != null ? request.getLoggedAt() : entry.getLoggedAt());
        return toResponse(mealEntryRepository.save(entry));
    }

    public void delete(int id) {
        MealEntry entry = getMealEntryOrThrow(id);
        mealEntryRepository.delete(entry);
    }

    public DailyNutritionSummaryResponse getDailySummary(LocalDate date) {
        List<MealEntry> entries = findByDate(date);

        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalCalories = 0;
        for (MealEntry entry : entries) {
            double factor = entry.getAmountGrams() / 100.0;
            FoodItem foodItem = entry.getFoodItem();
            totalProtein += factor * foodItem.getProteinPer100g();
            totalCarbs += factor * foodItem.getCarbsPer100g();
            totalFat += factor * foodItem.getFatPer100g();
            totalCalories += factor * foodItem.getCaloriesPer100g();
        }

        double finalProtein = totalProtein;
        double finalCarbs = totalCarbs;
        double finalFat = totalFat;
        double finalCalories = totalCalories;

        Optional<DailyMacroTarget> target = dailyMacroTargetRepository.findFirstByOrderByIdAsc();
        DailyMacroTargetResponse targetResponse = target.map(this::toTargetResponse).orElse(null);
        Double proteinDelta = target.map(t -> finalProtein - t.getTargetProtein()).orElse(null);
        Double carbsDelta = target.map(t -> finalCarbs - t.getTargetCarbs()).orElse(null);
        Double fatDelta = target.map(t -> finalFat - t.getTargetFat()).orElse(null);
        Double caloriesDelta = target.map(t -> finalCalories - t.getTargetCalories()).orElse(null);

        return new DailyNutritionSummaryResponse(
                date,
                totalProtein,
                totalCarbs,
                totalFat,
                totalCalories,
                targetResponse,
                proteinDelta,
                carbsDelta,
                fatDelta,
                caloriesDelta);
    }

    private List<MealEntry> findByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return mealEntryRepository.findByLoggedAtGreaterThanEqualAndLoggedAtLessThanOrderByLoggedAtAsc(start, end);
    }

    private MealEntry getMealEntryOrThrow(int id) {
        Optional<MealEntry> entry = mealEntryRepository.findById(id);
        return entry.orElseThrow(() -> new MealEntryNotFoundException(id));
    }

    private MealEntryResponse toResponse(MealEntry entry) {
        FoodItem foodItem = entry.getFoodItem();
        double factor = entry.getAmountGrams() / 100.0;
        return new MealEntryResponse(
                entry.getId(),
                foodItem.getId(),
                foodItem.getName(),
                entry.getAmountGrams(),
                entry.getLoggedAt(),
                factor * foodItem.getProteinPer100g(),
                factor * foodItem.getCarbsPer100g(),
                factor * foodItem.getFatPer100g(),
                factor * foodItem.getCaloriesPer100g());
    }

    private DailyMacroTargetResponse toTargetResponse(DailyMacroTarget target) {
        return new DailyMacroTargetResponse(
                target.getId(),
                target.getTargetProtein(),
                target.getTargetCarbs(),
                target.getTargetFat(),
                target.getTargetCalories());
    }
}

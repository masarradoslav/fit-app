package com.test.fit_app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MealEntryServiceTest {

    @Mock
    private MealEntryRepository mealEntryRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private DailyMacroTargetRepository dailyMacroTargetRepository;

    @InjectMocks
    private MealEntryService mealEntryService;

    private FoodItem chickenBreast() {
        return new FoodItem(5, "Chicken Breast", 31.0, 0.0, 3.6, 165.0);
    }

    private FoodItem rice() {
        return new FoodItem(6, "Rice", 2.7, 28.0, 0.3, 130.0);
    }

    @Test
    void create_shouldSaveMealEntryAndReturnMappedResponseWithComputedMacros() {
        FoodItem foodItem = chickenBreast();
        LocalDateTime loggedAt = LocalDateTime.of(2026, 8, 14, 12, 30);
        MealEntryRequest request = new MealEntryRequest(5, 200.0, loggedAt);
        MealEntry saved = new MealEntry(1, foodItem, 200.0, loggedAt);
        when(foodItemRepository.findById(5)).thenReturn(Optional.of(foodItem));
        when(mealEntryRepository.save(any(MealEntry.class))).thenReturn(saved);

        MealEntryResponse response = mealEntryService.create(request);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getFoodItemId()).isEqualTo(5);
        assertThat(response.getFoodItemName()).isEqualTo("Chicken Breast");
        assertThat(response.getAmountGrams()).isEqualTo(200.0);
        assertThat(response.getLoggedAt()).isEqualTo(loggedAt);
        assertThat(response.getProtein()).isEqualTo(62.0);
        assertThat(response.getCarbs()).isEqualTo(0.0);
        assertThat(response.getFat()).isEqualTo(7.2);
        assertThat(response.getCalories()).isEqualTo(330.0);
    }

    @Test
    void create_shouldDefaultLoggedAtToNow_whenNotProvided() {
        FoodItem foodItem = chickenBreast();
        MealEntryRequest request = new MealEntryRequest(5, 100.0, null);
        when(foodItemRepository.findById(5)).thenReturn(Optional.of(foodItem));
        ArgumentCaptor<MealEntry> captor = ArgumentCaptor.forClass(MealEntry.class);
        when(mealEntryRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        mealEntryService.create(request);

        assertThat(captor.getValue().getLoggedAt()).isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
    }

    @Test
    void create_shouldThrowFoodItemNotFoundException_whenFoodItemDoesNotExist() {
        when(foodItemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mealEntryService.create(new MealEntryRequest(99, 100.0, null)))
                .isInstanceOf(FoodItemNotFoundException.class);
        verify(mealEntryRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnMappedResponse_whenExists() {
        MealEntry entry = new MealEntry(1, chickenBreast(), 150.0, LocalDateTime.of(2026, 8, 14, 8, 0));
        when(mealEntryRepository.findById(1)).thenReturn(Optional.of(entry));

        MealEntryResponse response = mealEntryService.findById(1);

        assertThat(response.getFoodItemName()).isEqualTo("Chicken Breast");
        assertThat(response.getAmountGrams()).isEqualTo(150.0);
    }

    @Test
    void findById_shouldThrowMealEntryNotFoundException_whenNotExists() {
        when(mealEntryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mealEntryService.findById(99)).isInstanceOf(MealEntryNotFoundException.class);
    }

    @Test
    void findAll_shouldReturnAllEntries_whenDateIsNull() {
        MealEntry entry1 = new MealEntry(1, chickenBreast(), 150.0, LocalDateTime.of(2026, 8, 14, 8, 0));
        MealEntry entry2 = new MealEntry(2, rice(), 100.0, LocalDateTime.of(2026, 8, 14, 12, 0));
        when(mealEntryRepository.findAll()).thenReturn(List.of(entry1, entry2));

        List<MealEntryResponse> responses = mealEntryService.findAll(null);

        assertThat(responses).hasSize(2);
    }

    @Test
    void findAll_shouldReturnEntriesForThatDayOnly_whenDateProvided() {
        LocalDate date = LocalDate.of(2026, 8, 14);
        MealEntry entry = new MealEntry(1, chickenBreast(), 150.0, date.atTime(8, 0));
        when(mealEntryRepository.findByLoggedAtGreaterThanEqualAndLoggedAtLessThanOrderByLoggedAtAsc(
                        date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(entry));

        List<MealEntryResponse> responses = mealEntryService.findAll(date);

        assertThat(responses).hasSize(1);
        verify(mealEntryRepository, never()).findAll();
    }

    @Test
    void update_shouldUpdateAndReturnMappedResponse_whenExists() {
        MealEntry existing = new MealEntry(1, chickenBreast(), 100.0, LocalDateTime.of(2026, 8, 14, 8, 0));
        FoodItem newFoodItem = rice();
        LocalDateTime newLoggedAt = LocalDateTime.of(2026, 8, 14, 9, 0);
        MealEntryRequest request = new MealEntryRequest(6, 250.0, newLoggedAt);
        when(mealEntryRepository.findById(1)).thenReturn(Optional.of(existing));
        when(foodItemRepository.findById(6)).thenReturn(Optional.of(newFoodItem));
        when(mealEntryRepository.save(any(MealEntry.class))).thenReturn(existing);

        MealEntryResponse response = mealEntryService.update(1, request);

        assertThat(response.getFoodItemId()).isEqualTo(6);
        assertThat(response.getAmountGrams()).isEqualTo(250.0);
        assertThat(response.getLoggedAt()).isEqualTo(newLoggedAt);
        verify(mealEntryRepository).save(existing);
    }

    @Test
    void update_shouldThrowMealEntryNotFoundException_whenNotExists() {
        when(mealEntryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mealEntryService.update(99, new MealEntryRequest(5, 100.0, null)))
                .isInstanceOf(MealEntryNotFoundException.class);
        verify(mealEntryRepository, never()).save(any());
        verify(foodItemRepository, never()).findById(any());
    }

    @Test
    void delete_shouldDeleteMealEntry_whenExists() {
        MealEntry existing = new MealEntry(1, chickenBreast(), 100.0, LocalDateTime.of(2026, 8, 14, 8, 0));
        when(mealEntryRepository.findById(1)).thenReturn(Optional.of(existing));

        mealEntryService.delete(1);

        verify(mealEntryRepository).delete(existing);
    }

    @Test
    void delete_shouldThrowMealEntryNotFoundException_whenNotExists() {
        when(mealEntryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mealEntryService.delete(99)).isInstanceOf(MealEntryNotFoundException.class);
        verify(mealEntryRepository, never()).delete(any());
    }

    @Test
    void getDailySummary_shouldSumEntriesAndComputeDeltas_whenTargetExists() {
        LocalDate date = LocalDate.of(2026, 8, 14);
        MealEntry entry1 = new MealEntry(1, chickenBreast(), 200.0, date.atTime(8, 0));
        MealEntry entry2 = new MealEntry(2, rice(), 100.0, date.atTime(12, 0));
        when(mealEntryRepository.findByLoggedAtGreaterThanEqualAndLoggedAtLessThanOrderByLoggedAtAsc(
                        date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(entry1, entry2));
        DailyMacroTarget target = new DailyMacroTarget(1, 150.0, 200.0, 60.0, 2000.0);
        when(dailyMacroTargetRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(target));

        DailyNutritionSummaryResponse summary = mealEntryService.getDailySummary(date);

        assertThat(summary.getDate()).isEqualTo(date);
        assertThat(summary.getTotalProtein()).isCloseTo(64.7, within(0.0001));
        assertThat(summary.getTotalCarbs()).isCloseTo(28.0, within(0.0001));
        assertThat(summary.getTotalFat()).isCloseTo(7.5, within(0.0001));
        assertThat(summary.getTotalCalories()).isCloseTo(460.0, within(0.0001));
        assertThat(summary.getTarget()).isNotNull();
        assertThat(summary.getProteinDelta()).isCloseTo(64.7 - 150.0, within(0.0001));
        assertThat(summary.getCarbsDelta()).isCloseTo(28.0 - 200.0, within(0.0001));
        assertThat(summary.getFatDelta()).isCloseTo(7.5 - 60.0, within(0.0001));
        assertThat(summary.getCaloriesDelta()).isCloseTo(460.0 - 2000.0, within(0.0001));
    }

    @Test
    void getDailySummary_shouldReturnZeroTotalsAndNullTargetAndDeltas_whenNoEntriesAndNoTarget() {
        LocalDate date = LocalDate.of(2026, 8, 14);
        when(mealEntryRepository.findByLoggedAtGreaterThanEqualAndLoggedAtLessThanOrderByLoggedAtAsc(
                        date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of());
        when(dailyMacroTargetRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        DailyNutritionSummaryResponse summary = mealEntryService.getDailySummary(date);

        assertThat(summary.getTotalProtein()).isEqualTo(0.0);
        assertThat(summary.getTotalCarbs()).isEqualTo(0.0);
        assertThat(summary.getTotalFat()).isEqualTo(0.0);
        assertThat(summary.getTotalCalories()).isEqualTo(0.0);
        assertThat(summary.getTarget()).isNull();
        assertThat(summary.getProteinDelta()).isNull();
        assertThat(summary.getCarbsDelta()).isNull();
        assertThat(summary.getFatDelta()).isNull();
        assertThat(summary.getCaloriesDelta()).isNull();
    }
}

package com.test.fit_app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.fit_app.dto.PersonalRecordRequest;
import com.test.fit_app.dto.PersonalRecordResponse;
import com.test.fit_app.exception.ExerciseNotFoundException;
import com.test.fit_app.exception.PersonalRecordNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.model.PersonalRecord;
import com.test.fit_app.model.enums.Muscle;
import com.test.fit_app.model.enums.Musclegroups;
import com.test.fit_app.repository.ExerciseRepository;
import com.test.fit_app.repository.PersonalRecordRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalRecordServiceTest {

    @Mock
    private PersonalRecordRepository personalRecordRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private PersonalRecordService personalRecordService;

    private Exercise benchPress() {
        return new Exercise(2, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
    }

    @Test
    void create_shouldSavePersonalRecordAndReturnMappedResponse_whenExerciseExists() {
        Exercise exercise = benchPress();
        PersonalRecordRequest request = new PersonalRecordRequest(2, 7, LocalDate.of(2026, 8, 29), 100.0);
        PersonalRecord saved = new PersonalRecord(1, exercise, 7, LocalDate.of(2026, 8, 29), 100.0);

        when(exerciseRepository.findById(2)).thenReturn(Optional.of(exercise));
        when(personalRecordRepository.save(any(PersonalRecord.class))).thenReturn(saved);

        PersonalRecordResponse response = personalRecordService.create(request);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getExerciseId()).isEqualTo(2);
        assertThat(response.getExerciseName()).isEqualTo("Bench Press");
        assertThat(response.getUserId()).isEqualTo(7);
        assertThat(response.getDate()).isEqualTo(LocalDate.of(2026, 8, 29));
        assertThat(response.getWeightKg()).isEqualTo(100.0);
    }

    @Test
    void create_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        PersonalRecordRequest request = new PersonalRecordRequest(99, 7, LocalDate.of(2026, 8, 29), 100.0);
        when(exerciseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personalRecordService.create(request))
                .isInstanceOf(ExerciseNotFoundException.class);
        verify(personalRecordRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnMappedResponse_whenPersonalRecordExists() {
        PersonalRecord personalRecord = new PersonalRecord(1, benchPress(), 7, LocalDate.of(2026, 8, 29), 100.0);
        when(personalRecordRepository.findById(1)).thenReturn(Optional.of(personalRecord));

        PersonalRecordResponse response = personalRecordService.findById(1);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getWeightKg()).isEqualTo(100.0);
    }

    @Test
    void findById_shouldThrowPersonalRecordNotFoundException_whenPersonalRecordDoesNotExist() {
        when(personalRecordRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personalRecordService.findById(99))
                .isInstanceOf(PersonalRecordNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_shouldReturnAllPersonalRecordsMappedToResponses() {
        PersonalRecord first = new PersonalRecord(1, benchPress(), 7, LocalDate.of(2026, 8, 29), 100.0);
        PersonalRecord second = new PersonalRecord(2, benchPress(), 7, LocalDate.of(2026, 9, 5), 102.5);
        when(personalRecordRepository.findAll()).thenReturn(List.of(first, second));

        List<PersonalRecordResponse> responses = personalRecordService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(PersonalRecordResponse::getWeightKg)
                .containsExactly(100.0, 102.5);
    }

    @Test
    void update_shouldUpdateAndReturnMappedResponse_whenPersonalRecordAndExerciseExist() {
        Exercise exercise = benchPress();
        PersonalRecord existing = new PersonalRecord(1, exercise, 7, LocalDate.of(2026, 8, 29), 100.0);
        PersonalRecordRequest request = new PersonalRecordRequest(2, 7, LocalDate.of(2026, 9, 5), 102.5);

        when(personalRecordRepository.findById(1)).thenReturn(Optional.of(existing));
        when(exerciseRepository.findById(2)).thenReturn(Optional.of(exercise));
        when(personalRecordRepository.save(any(PersonalRecord.class))).thenReturn(existing);

        PersonalRecordResponse response = personalRecordService.update(1, request);

        assertThat(response.getWeightKg()).isEqualTo(102.5);
        assertThat(response.getDate()).isEqualTo(LocalDate.of(2026, 9, 5));
        verify(personalRecordRepository).save(existing);
    }

    @Test
    void update_shouldThrowPersonalRecordNotFoundException_whenPersonalRecordDoesNotExist() {
        PersonalRecordRequest request = new PersonalRecordRequest(2, 7, LocalDate.of(2026, 9, 5), 102.5);
        when(personalRecordRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personalRecordService.update(99, request))
                .isInstanceOf(PersonalRecordNotFoundException.class);
        verify(personalRecordRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeletePersonalRecord_whenPersonalRecordExists() {
        PersonalRecord existing = new PersonalRecord(1, benchPress(), 7, LocalDate.of(2026, 8, 29), 100.0);
        when(personalRecordRepository.findById(1)).thenReturn(Optional.of(existing));

        personalRecordService.delete(1);

        verify(personalRecordRepository, times(1)).delete(existing);
    }

    @Test
    void delete_shouldThrowPersonalRecordNotFoundException_whenPersonalRecordDoesNotExist() {
        when(personalRecordRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personalRecordService.delete(99))
                .isInstanceOf(PersonalRecordNotFoundException.class);
        verify(personalRecordRepository, never()).delete(any());
    }
}

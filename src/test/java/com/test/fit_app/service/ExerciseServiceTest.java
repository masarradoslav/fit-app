package com.test.fit_app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.fit_app.dto.ExerciseRequest;
import com.test.fit_app.dto.ExerciseResponse;
import com.test.fit_app.exception.ExerciseNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.model.enums.Muscle;
import com.test.fit_app.model.enums.Musclegroups;
import com.test.fit_app.repository.ExerciseRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void create_shouldSaveExerciseAndReturnMappedResponse() {
        ExerciseRequest request = new ExerciseRequest("Squat", Musclegroups.LEGS, Muscle.QUADS);
        Exercise saved = new Exercise(1, "Squat", Musclegroups.LEGS, Muscle.QUADS);
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(saved);

        ExerciseResponse response = exerciseService.create(request);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("Squat");
        assertThat(response.getMuscleGroup()).isEqualTo(Musclegroups.LEGS);
        assertThat(response.getMuscle()).isEqualTo(Muscle.QUADS);
    }

    @Test
    void findById_shouldReturnMappedResponse_whenExerciseExists() {
        Exercise exercise = new Exercise(1, "Squat", Musclegroups.LEGS, Muscle.QUADS);
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));

        ExerciseResponse response = exerciseService.findById(1);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("Squat");
    }

    @Test
    void findById_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.findById(99))
                .isInstanceOf(ExerciseNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_shouldReturnAllExercisesMappedToResponses() {
        Exercise squat = new Exercise(1, "Squat", Musclegroups.LEGS, Muscle.QUADS);
        Exercise benchPress = new Exercise(2, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        when(exerciseRepository.findAll()).thenReturn(List.of(squat, benchPress));

        List<ExerciseResponse> responses = exerciseService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(ExerciseResponse::getName)
                .containsExactly("Squat", "Bench Press");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoExercisesExist() {
        when(exerciseRepository.findAll()).thenReturn(List.of());

        List<ExerciseResponse> responses = exerciseService.findAll();

        assertThat(responses).isEmpty();
    }

    @Test
    void update_shouldUpdateAndReturnMappedResponse_whenExerciseExists() {
        Exercise existing = new Exercise(1, "Squat", Musclegroups.LEGS, Muscle.QUADS);
        ExerciseRequest request = new ExerciseRequest("Front Squat", Musclegroups.LEGS, Muscle.QUADS);
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(existing));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(existing);

        ExerciseResponse response = exerciseService.update(1, request);

        assertThat(response.getName()).isEqualTo("Front Squat");
        verify(exerciseRepository).save(existing);
    }

    @Test
    void update_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        ExerciseRequest request = new ExerciseRequest("Front Squat", Musclegroups.LEGS, Muscle.QUADS);
        when(exerciseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.update(99, request))
                .isInstanceOf(ExerciseNotFoundException.class);
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteExercise_whenExerciseExists() {
        Exercise existing = new Exercise(1, "Squat", Musclegroups.LEGS, Muscle.QUADS);
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(existing));

        exerciseService.delete(1);

        verify(exerciseRepository, times(1)).delete(existing);
    }

    @Test
    void delete_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.delete(99))
                .isInstanceOf(ExerciseNotFoundException.class);
        verify(exerciseRepository, never()).delete(any());
    }
}

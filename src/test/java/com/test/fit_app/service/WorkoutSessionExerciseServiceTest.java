package com.test.fit_app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.fit_app.dto.WorkoutSessionExerciseRequest;
import com.test.fit_app.dto.WorkoutSessionExerciseResponse;
import com.test.fit_app.exception.ExerciseNotFoundException;
import com.test.fit_app.exception.WorkoutSessionExerciseNotFoundException;
import com.test.fit_app.exception.WorkoutSessionNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.model.SetLog;
import com.test.fit_app.model.WorkoutSession;
import com.test.fit_app.model.WorkoutSessionExercise;
import com.test.fit_app.model.enums.Muscle;
import com.test.fit_app.model.enums.Musclegroups;
import com.test.fit_app.repository.ExerciseRepository;
import com.test.fit_app.repository.SetLogRepository;
import com.test.fit_app.repository.WorkoutSessionExerciseRepository;
import com.test.fit_app.repository.WorkoutSessionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkoutSessionExerciseServiceTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private WorkoutSessionExerciseRepository workoutSessionExerciseRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private SetLogRepository setLogRepository;

    @InjectMocks
    private WorkoutSessionExerciseService workoutSessionExerciseService;

    @Test
    void addExercise_shouldAddExerciseToSession_whenSessionAndExerciseExist() {
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDate.of(2026, 8, 8));
        Exercise benchPress = new Exercise(2, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise saved = new WorkoutSessionExercise(10, session, benchPress, 0);

        when(workoutSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(exerciseRepository.findById(2)).thenReturn(Optional.of(benchPress));
        when(workoutSessionExerciseRepository.countByWorkoutSessionId(1)).thenReturn(0);
        when(workoutSessionExerciseRepository.save(any(WorkoutSessionExercise.class))).thenReturn(saved);
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10)).thenReturn(List.of());

        WorkoutSessionExerciseResponse response =
                workoutSessionExerciseService.addExercise(1, new WorkoutSessionExerciseRequest(2));

        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getExerciseId()).isEqualTo(2);
        assertThat(response.getExerciseName()).isEqualTo("Bench Press");
        assertThat(response.getPosition()).isEqualTo(0);
        assertThat(response.getSets()).isEmpty();
    }

    @Test
    void addExercise_shouldThrowWorkoutSessionNotFoundException_whenSessionDoesNotExist() {
        when(workoutSessionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionExerciseService.addExercise(99, new WorkoutSessionExerciseRequest(2)))
                .isInstanceOf(WorkoutSessionNotFoundException.class);
        verify(workoutSessionExerciseRepository, never()).save(any());
    }

    @Test
    void addExercise_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDate.of(2026, 8, 8));
        when(workoutSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(exerciseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionExerciseService.addExercise(1, new WorkoutSessionExerciseRequest(99)))
                .isInstanceOf(ExerciseNotFoundException.class);
        verify(workoutSessionExerciseRepository, never()).save(any());
    }

    @Test
    void removeExercise_shouldDeleteSessionExerciseAndItsSetLogs_whenExists() {
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDate.of(2026, 8, 8));
        Exercise benchPress = new Exercise(2, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise sessionExercise = new WorkoutSessionExercise(10, session, benchPress, 0);
        SetLog setLog = new SetLog(100, sessionExercise, 1, 8, 60.0);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(sessionExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10)).thenReturn(List.of(setLog));

        workoutSessionExerciseService.removeExercise(1, 10);

        verify(setLogRepository).deleteAll(List.of(setLog));
        verify(workoutSessionExerciseRepository).delete(sessionExercise);
    }

    @Test
    void removeExercise_shouldThrowWorkoutSessionExerciseNotFoundException_whenNotFound() {
        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionExerciseService.removeExercise(1, 99))
                .isInstanceOf(WorkoutSessionExerciseNotFoundException.class);
        verify(workoutSessionExerciseRepository, never()).delete(any());
    }
}

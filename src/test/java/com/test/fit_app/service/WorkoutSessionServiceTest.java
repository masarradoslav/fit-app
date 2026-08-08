package com.test.fit_app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.fit_app.dto.WorkoutSessionRequest;
import com.test.fit_app.dto.WorkoutSessionResponse;
import com.test.fit_app.exception.WorkoutSessionNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.model.SetLog;
import com.test.fit_app.model.WorkoutSession;
import com.test.fit_app.model.WorkoutSessionExercise;
import com.test.fit_app.model.enums.Muscle;
import com.test.fit_app.model.enums.Musclegroups;
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
class WorkoutSessionServiceTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private WorkoutSessionExerciseRepository workoutSessionExerciseRepository;

    @Mock
    private SetLogRepository setLogRepository;

    @InjectMocks
    private WorkoutSessionService workoutSessionService;

    @Test
    void create_shouldSaveSessionAndReturnMappedResponse() {
        LocalDate date = LocalDate.of(2026, 8, 8);
        WorkoutSessionRequest request = new WorkoutSessionRequest("Push Day", date);
        WorkoutSession saved = new WorkoutSession(1, "Push Day", date);
        when(workoutSessionRepository.save(any(WorkoutSession.class))).thenReturn(saved);
        when(workoutSessionExerciseRepository.findByWorkoutSessionIdOrderByPositionAsc(1)).thenReturn(List.of());

        WorkoutSessionResponse response = workoutSessionService.create(request);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("Push Day");
        assertThat(response.getDate()).isEqualTo(date);
        assertThat(response.getExercises()).isEmpty();
        assertThat(response.getMuscleGroupsHit()).isEmpty();
    }

    @Test
    void findById_shouldReturnMappedResponseWithNestedExercisesAndSets_whenSessionExists() {
        LocalDate date = LocalDate.of(2026, 8, 8);
        WorkoutSession session = new WorkoutSession(1, "Push Day", date);
        Exercise benchPress = new Exercise(2, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise sessionExercise = new WorkoutSessionExercise(10, session, benchPress, 0);
        SetLog firstSet = new SetLog(100, sessionExercise, 1, 8, 60.0);
        SetLog secondSet = new SetLog(101, sessionExercise, 2, 8, 60.0);

        when(workoutSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(workoutSessionExerciseRepository.findByWorkoutSessionIdOrderByPositionAsc(1))
                .thenReturn(List.of(sessionExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10))
                .thenReturn(List.of(firstSet, secondSet));

        WorkoutSessionResponse response = workoutSessionService.findById(1);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getMuscleGroupsHit()).containsExactly(Musclegroups.CHEST);
        assertThat(response.getExercises()).hasSize(1);
        assertThat(response.getExercises().get(0).getExerciseName()).isEqualTo("Bench Press");
        assertThat(response.getExercises().get(0).getSets()).hasSize(2);
        assertThat(response.getExercises().get(0).getSets().get(0).getReps()).isEqualTo(8);
        assertThat(response.getExercises().get(0).getSets().get(0).getWeightKg()).isEqualTo(60.0);
    }

    @Test
    void findById_shouldThrowWorkoutSessionNotFoundException_whenSessionDoesNotExist() {
        when(workoutSessionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionService.findById(99))
                .isInstanceOf(WorkoutSessionNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoSessionsExist() {
        when(workoutSessionRepository.findAll()).thenReturn(List.of());

        List<WorkoutSessionResponse> responses = workoutSessionService.findAll();

        assertThat(responses).isEmpty();
    }

    @Test
    void update_shouldUpdateAndReturnMappedResponse_whenSessionExists() {
        LocalDate oldDate = LocalDate.of(2026, 8, 8);
        LocalDate newDate = LocalDate.of(2026, 8, 9);
        WorkoutSession existing = new WorkoutSession(1, "Push Day", oldDate);
        WorkoutSessionRequest request = new WorkoutSessionRequest("Pull Day", newDate);
        when(workoutSessionRepository.findById(1)).thenReturn(Optional.of(existing));
        when(workoutSessionRepository.save(any(WorkoutSession.class))).thenReturn(existing);
        when(workoutSessionExerciseRepository.findByWorkoutSessionIdOrderByPositionAsc(1)).thenReturn(List.of());

        WorkoutSessionResponse response = workoutSessionService.update(1, request);

        assertThat(response.getName()).isEqualTo("Pull Day");
        assertThat(response.getDate()).isEqualTo(newDate);
        verify(workoutSessionRepository).save(existing);
    }

    @Test
    void update_shouldThrowWorkoutSessionNotFoundException_whenSessionDoesNotExist() {
        WorkoutSessionRequest request = new WorkoutSessionRequest("Pull Day", LocalDate.of(2026, 8, 9));
        when(workoutSessionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionService.update(99, request))
                .isInstanceOf(WorkoutSessionNotFoundException.class);
    }

    @Test
    void delete_shouldCascadeDeleteExercisesAndSetLogs_whenSessionExists() {
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDate.of(2026, 8, 8));
        Exercise benchPress = new Exercise(2, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise sessionExercise = new WorkoutSessionExercise(10, session, benchPress, 0);
        SetLog setLog = new SetLog(100, sessionExercise, 1, 8, 60.0);

        when(workoutSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(workoutSessionExerciseRepository.findByWorkoutSessionIdOrderByPositionAsc(1))
                .thenReturn(List.of(sessionExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10))
                .thenReturn(List.of(setLog));

        workoutSessionService.delete(1);

        verify(setLogRepository).deleteAll(List.of(setLog));
        verify(workoutSessionExerciseRepository).deleteAll(List.of(sessionExercise));
        verify(workoutSessionRepository).delete(session);
    }

    @Test
    void delete_shouldThrowWorkoutSessionNotFoundException_whenSessionDoesNotExist() {
        when(workoutSessionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionService.delete(99))
                .isInstanceOf(WorkoutSessionNotFoundException.class);
    }
}

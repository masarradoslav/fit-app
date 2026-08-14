package com.test.fit_app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.fit_app.dto.SetLogRequest;
import com.test.fit_app.dto.SetLogResponse;
import com.test.fit_app.exception.SetLogNotFoundException;
import com.test.fit_app.exception.WorkoutSessionExerciseNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.model.SetLog;
import com.test.fit_app.model.WorkoutSession;
import com.test.fit_app.model.WorkoutSessionExercise;
import com.test.fit_app.model.enums.Muscle;
import com.test.fit_app.model.enums.Musclegroups;
import com.test.fit_app.repository.SetLogRepository;
import com.test.fit_app.repository.WorkoutSessionExerciseRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SetLogServiceTest {

    @Mock
    private WorkoutSessionExerciseRepository workoutSessionExerciseRepository;

    @Mock
    private SetLogRepository setLogRepository;

    @InjectMocks
    private SetLogService setLogService;

    private WorkoutSessionExercise sessionExercise() {
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 8, 10, 0));
        Exercise benchPress = new Exercise(2, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        return new WorkoutSessionExercise(10, session, benchPress, 0);
    }

    @Test
    void addSet_shouldSaveSetLogAndReturnMappedResponse_whenSessionExerciseExists() {
        WorkoutSessionExercise sessionExercise = sessionExercise();
        SetLogRequest request = new SetLogRequest(8, 60.0);
        SetLog saved = new SetLog(100, sessionExercise, 1, 8, 60.0);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(sessionExercise));
        when(setLogRepository.countByWorkoutSessionExerciseId(10)).thenReturn(0);
        when(setLogRepository.save(any(SetLog.class))).thenReturn(saved);

        SetLogResponse response = setLogService.addSet(1, 10, request);

        assertThat(response.getId()).isEqualTo(100);
        assertThat(response.getSetIndex()).isEqualTo(1);
        assertThat(response.getReps()).isEqualTo(8);
        assertThat(response.getWeightKg()).isEqualTo(60.0);
    }

    @Test
    void addSet_shouldThrowWorkoutSessionExerciseNotFoundException_whenSessionExerciseDoesNotExist() {
        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> setLogService.addSet(1, 99, new SetLogRequest(8, 60.0)))
                .isInstanceOf(WorkoutSessionExerciseNotFoundException.class);
        verify(setLogRepository, never()).save(any());
    }

    @Test
    void updateSet_shouldUpdateAndReturnMappedResponse_whenSetLogExists() {
        WorkoutSessionExercise sessionExercise = sessionExercise();
        SetLog existing = new SetLog(100, sessionExercise, 1, 8, 60.0);
        SetLogRequest request = new SetLogRequest(10, 62.5);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(sessionExercise));
        when(setLogRepository.findByIdAndWorkoutSessionExerciseId(100, 10)).thenReturn(Optional.of(existing));
        when(setLogRepository.save(any(SetLog.class))).thenReturn(existing);

        SetLogResponse response = setLogService.updateSet(1, 10, 100, request);

        assertThat(response.getReps()).isEqualTo(10);
        assertThat(response.getWeightKg()).isEqualTo(62.5);
        verify(setLogRepository).save(existing);
    }

    @Test
    void updateSet_shouldThrowSetLogNotFoundException_whenSetLogDoesNotExist() {
        WorkoutSessionExercise sessionExercise = sessionExercise();
        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(sessionExercise));
        when(setLogRepository.findByIdAndWorkoutSessionExerciseId(999, 10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> setLogService.updateSet(1, 10, 999, new SetLogRequest(8, 60.0)))
                .isInstanceOf(SetLogNotFoundException.class);
        verify(setLogRepository, never()).save(any());
    }

    @Test
    void deleteSet_shouldDeleteSetLog_whenExists() {
        WorkoutSessionExercise sessionExercise = sessionExercise();
        SetLog existing = new SetLog(100, sessionExercise, 1, 8, 60.0);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(sessionExercise));
        when(setLogRepository.findByIdAndWorkoutSessionExerciseId(100, 10)).thenReturn(Optional.of(existing));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10))
                .thenReturn(List.of());

        setLogService.deleteSet(1, 10, 100);

        verify(setLogRepository).delete(existing);
    }

    @Test
    void deleteSet_shouldRenumberLaterSets_soNextAddedSetDoesNotCollide() {
        WorkoutSessionExercise sessionExercise = sessionExercise();
        SetLog set1 = new SetLog(100, sessionExercise, 1, 8, 60.0);
        SetLog set2 = new SetLog(101, sessionExercise, 2, 8, 60.0);
        SetLog set3 = new SetLog(102, sessionExercise, 3, 8, 60.0);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(sessionExercise));
        when(setLogRepository.findByIdAndWorkoutSessionExerciseId(101, 10)).thenReturn(Optional.of(set2));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10))
                .thenReturn(List.of(set1, set3));

        setLogService.deleteSet(1, 10, 101);

        verify(setLogRepository).delete(set2);
        assertThat(set1.getSetIndex()).isEqualTo(1);
        assertThat(set3.getSetIndex()).isEqualTo(2);
    }

    @Test
    void deleteSet_shouldThrowSetLogNotFoundException_whenNotFound() {
        WorkoutSessionExercise sessionExercise = sessionExercise();
        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(sessionExercise));
        when(setLogRepository.findByIdAndWorkoutSessionExerciseId(999, 10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> setLogService.deleteSet(1, 10, 999))
                .isInstanceOf(SetLogNotFoundException.class);
        verify(setLogRepository, never()).delete(any());
    }
}

package com.test.fit_app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.fit_app.dto.ExerciseComparisonResponse;
import com.test.fit_app.dto.SetComparisonResponse;
import com.test.fit_app.dto.SetComparisonStatus;
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
import java.time.LocalDateTime;
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
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 8, 10, 0));
        Exercise benchPress = new Exercise(2, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise saved = new WorkoutSessionExercise(10, session, benchPress, 0);

        when(workoutSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(exerciseRepository.findById(2)).thenReturn(Optional.of(benchPress));
        when(workoutSessionExerciseRepository.countByWorkoutSessionId(1)).thenReturn(0);
        when(workoutSessionExerciseRepository.save(any(WorkoutSessionExercise.class))).thenReturn(saved);
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10)).thenReturn(List.of());

        WorkoutSessionExerciseResponse response = workoutSessionExerciseService.addExercise(1, new WorkoutSessionExerciseRequest(2));

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
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 8, 10, 0));
        when(workoutSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(exerciseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutSessionExerciseService.addExercise(1, new WorkoutSessionExerciseRequest(99)))
                .isInstanceOf(ExerciseNotFoundException.class);
        verify(workoutSessionExerciseRepository, never()).save(any());
    }

    @Test
    void removeExercise_shouldDeleteSessionExerciseAndItsSetLogs_whenExists() {
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 8, 10, 0));
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

    @Test
    void compareWithPrevious_shouldReturnMatchedSetsWithDeltas_whenSetCountsAreEqual() {
        WorkoutSession currentSession = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 9, 10, 0));
        WorkoutSession previousSession = new WorkoutSession(2, "Push Day", LocalDateTime.of(2026, 8, 1, 10, 0));
        Exercise benchPress = new Exercise(3, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise currentExercise = new WorkoutSessionExercise(10, currentSession, benchPress, 0);
        WorkoutSessionExercise previousExercise = new WorkoutSessionExercise(20, previousSession, benchPress, 0);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(currentExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10))
                .thenReturn(List.of(
                        new SetLog(100, currentExercise, 1, 8, 62.5), new SetLog(101, currentExercise, 2, 8, 62.5)));
        when(workoutSessionExerciseRepository
                        .findFirstByExercise_IdAndWorkoutSession_DateLessThanOrderByWorkoutSession_DateDescIdDesc(
                                3, LocalDateTime.of(2026, 8, 9, 10, 0)))
                .thenReturn(Optional.of(previousExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(20))
                .thenReturn(List.of(
                        new SetLog(200, previousExercise, 1, 8, 60.0),
                        new SetLog(201, previousExercise, 2, 7, 60.0)));

        ExerciseComparisonResponse response = workoutSessionExerciseService.compareWithPrevious(1, 10);

        assertThat(response.isHasPreviousSession()).isTrue();
        assertThat(response.getSets()).hasSize(2);

        SetComparisonResponse firstSet = response.getSets().get(0);
        assertThat(firstSet.getStatus()).isEqualTo(SetComparisonStatus.MATCHED);
        assertThat(firstSet.getWeightDeltaKg()).isEqualTo(2.5);
        assertThat(firstSet.getRepsDelta()).isEqualTo(0);

        SetComparisonResponse secondSet = response.getSets().get(1);
        assertThat(secondSet.getStatus()).isEqualTo(SetComparisonStatus.MATCHED);
        assertThat(secondSet.getWeightDeltaKg()).isEqualTo(2.5);
        assertThat(secondSet.getRepsDelta()).isEqualTo(1);
    }

    @Test
    void compareWithPrevious_shouldFlagExtraSet_whenCurrentSessionHasMoreSetsThanPrevious() {
        WorkoutSession currentSession = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 9, 10, 0));
        WorkoutSession previousSession = new WorkoutSession(2, "Push Day", LocalDateTime.of(2026, 8, 1, 10, 0));
        Exercise benchPress = new Exercise(3, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise currentExercise = new WorkoutSessionExercise(10, currentSession, benchPress, 0);
        WorkoutSessionExercise previousExercise = new WorkoutSessionExercise(20, previousSession, benchPress, 0);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(currentExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10))
                .thenReturn(List.of(
                        new SetLog(100, currentExercise, 1, 8, 60.0), new SetLog(101, currentExercise, 2, 8, 60.0)));
        when(workoutSessionExerciseRepository
                        .findFirstByExercise_IdAndWorkoutSession_DateLessThanOrderByWorkoutSession_DateDescIdDesc(
                                3, LocalDateTime.of(2026, 8, 9, 10, 0)))
                .thenReturn(Optional.of(previousExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(20))
                .thenReturn(List.of(new SetLog(200, previousExercise, 1, 8, 60.0)));

        ExerciseComparisonResponse response = workoutSessionExerciseService.compareWithPrevious(1, 10);

        assertThat(response.getSets()).hasSize(2);
        assertThat(response.getSets().get(0).getStatus()).isEqualTo(SetComparisonStatus.MATCHED);

        SetComparisonResponse extraSet = response.getSets().get(1);
        assertThat(extraSet.getStatus()).isEqualTo(SetComparisonStatus.EXTRA_SET);
        assertThat(extraSet.getCurrent()).isNotNull();
        assertThat(extraSet.getPrevious()).isNull();
        assertThat(extraSet.getWeightDeltaKg()).isNull();
        assertThat(extraSet.getRepsDelta()).isNull();
    }

    @Test
    void compareWithPrevious_shouldFlagMissingSet_whenPreviousSessionHadMoreSetsThanCurrent() {
        WorkoutSession currentSession = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 9, 10, 0));
        WorkoutSession previousSession = new WorkoutSession(2, "Push Day", LocalDateTime.of(2026, 8, 1, 10, 0));
        Exercise benchPress = new Exercise(3, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise currentExercise = new WorkoutSessionExercise(10, currentSession, benchPress, 0);
        WorkoutSessionExercise previousExercise = new WorkoutSessionExercise(20, previousSession, benchPress, 0);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(currentExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10))
                .thenReturn(List.of(new SetLog(100, currentExercise, 1, 8, 60.0)));
        when(workoutSessionExerciseRepository
                        .findFirstByExercise_IdAndWorkoutSession_DateLessThanOrderByWorkoutSession_DateDescIdDesc(
                                3, LocalDateTime.of(2026, 8, 9, 10, 0)))
                .thenReturn(Optional.of(previousExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(20))
                .thenReturn(List.of(
                        new SetLog(200, previousExercise, 1, 8, 60.0),
                        new SetLog(201, previousExercise, 2, 8, 60.0)));

        ExerciseComparisonResponse response = workoutSessionExerciseService.compareWithPrevious(1, 10);

        assertThat(response.getSets()).hasSize(2);
        SetComparisonResponse missingSet = response.getSets().get(1);
        assertThat(missingSet.getStatus()).isEqualTo(SetComparisonStatus.MISSING_SET);
        assertThat(missingSet.getCurrent()).isNull();
        assertThat(missingSet.getPrevious()).isNotNull();
    }

    @Test
    void compareWithPrevious_shouldReturnNoPreviousSession_whenExerciseNeverPerformedBefore() {
        WorkoutSession currentSession = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 9, 10, 0));
        Exercise benchPress = new Exercise(3, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        WorkoutSessionExercise currentExercise = new WorkoutSessionExercise(10, currentSession, benchPress, 0);

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(currentExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10))
                .thenReturn(List.of(new SetLog(100, currentExercise, 1, 8, 60.0)));
        when(workoutSessionExerciseRepository
                        .findFirstByExercise_IdAndWorkoutSession_DateLessThanOrderByWorkoutSession_DateDescIdDesc(
                                3, LocalDateTime.of(2026, 8, 9, 10, 0)))
                .thenReturn(Optional.empty());

        ExerciseComparisonResponse response = workoutSessionExerciseService.compareWithPrevious(1, 10);

        assertThat(response.isHasPreviousSession()).isFalse();
        assertThat(response.getSets()).hasSize(1);
        assertThat(response.getSets().get(0).getStatus()).isEqualTo(SetComparisonStatus.EXTRA_SET);
    }

    @Test
    void compareSessionWithPrevious_shouldReturnOneComparisonPerExerciseInSession() {
        WorkoutSession session = new WorkoutSession(1, "Push Day", LocalDateTime.of(2026, 8, 9, 10, 0));
        Exercise benchPress = new Exercise(3, "Bench Press", Musclegroups.CHEST, Muscle.UPPER_CHEST);
        Exercise overheadPress = new Exercise(4, "Overhead Press", Musclegroups.SHOULDERS, Muscle.FRONT_DELTS);
        WorkoutSessionExercise benchSessionExercise = new WorkoutSessionExercise(10, session, benchPress, 0);
        WorkoutSessionExercise ohpSessionExercise = new WorkoutSessionExercise(11, session, overheadPress, 1);

        when(workoutSessionRepository.existsById(1)).thenReturn(true);
        when(workoutSessionExerciseRepository.findByWorkoutSessionIdOrderByPositionAsc(1))
                .thenReturn(List.of(benchSessionExercise, ohpSessionExercise));

        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(10, 1))
                .thenReturn(Optional.of(benchSessionExercise));
        when(workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(11, 1))
                .thenReturn(Optional.of(ohpSessionExercise));
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(10)).thenReturn(List.of());
        when(setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(11)).thenReturn(List.of());
        when(workoutSessionExerciseRepository
                        .findFirstByExercise_IdAndWorkoutSession_DateLessThanOrderByWorkoutSession_DateDescIdDesc(
                                3, LocalDateTime.of(2026, 8, 9, 10, 0)))
                .thenReturn(Optional.empty());
        when(workoutSessionExerciseRepository
                        .findFirstByExercise_IdAndWorkoutSession_DateLessThanOrderByWorkoutSession_DateDescIdDesc(
                                4, LocalDateTime.of(2026, 8, 9, 10, 0)))
                .thenReturn(Optional.empty());

        List<ExerciseComparisonResponse> responses = workoutSessionExerciseService.compareSessionWithPrevious(1);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getExerciseId()).isEqualTo(3);
        assertThat(responses.get(1).getExerciseId()).isEqualTo(4);
    }

    @Test
    void compareSessionWithPrevious_shouldThrowWorkoutSessionNotFoundException_whenSessionDoesNotExist() {
        when(workoutSessionRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> workoutSessionExerciseService.compareSessionWithPrevious(99))
                .isInstanceOf(WorkoutSessionNotFoundException.class);
        verify(workoutSessionExerciseRepository, never()).findByWorkoutSessionIdOrderByPositionAsc(anyInt());
    }
}

package com.test.fit_app.service;

import com.test.fit_app.dto.SetLogResponse;
import com.test.fit_app.dto.WorkoutSessionExerciseRequest;
import com.test.fit_app.dto.WorkoutSessionExerciseResponse;
import com.test.fit_app.exception.ExerciseNotFoundException;
import com.test.fit_app.exception.WorkoutSessionExerciseNotFoundException;
import com.test.fit_app.exception.WorkoutSessionNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.model.WorkoutSession;
import com.test.fit_app.model.WorkoutSessionExercise;
import com.test.fit_app.repository.ExerciseRepository;
import com.test.fit_app.repository.SetLogRepository;
import com.test.fit_app.repository.WorkoutSessionExerciseRepository;
import com.test.fit_app.repository.WorkoutSessionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutSessionExerciseService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutSessionExerciseRepository workoutSessionExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final SetLogRepository setLogRepository;

    @Transactional
    public WorkoutSessionExerciseResponse addExercise(int sessionId, WorkoutSessionExerciseRequest request) {
        WorkoutSession session = workoutSessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new WorkoutSessionNotFoundException(sessionId));
        Exercise exercise = exerciseRepository
                .findById(request.getExerciseId())
                .orElseThrow(() -> new ExerciseNotFoundException(request.getExerciseId()));

        WorkoutSessionExercise sessionExercise = new WorkoutSessionExercise();
        sessionExercise.setWorkoutSession(session);
        sessionExercise.setExercise(exercise);
        sessionExercise.setPosition(workoutSessionExerciseRepository.countByWorkoutSessionId(sessionId));

        return toResponse(workoutSessionExerciseRepository.save(sessionExercise));
    }

    @Transactional
    public void removeExercise(int sessionId, int workoutSessionExerciseId) {
        WorkoutSessionExercise sessionExercise = getOrThrow(sessionId, workoutSessionExerciseId);
        setLogRepository.deleteAll(
                setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(sessionExercise.getId()));
        workoutSessionExerciseRepository.delete(sessionExercise);
    }

    private WorkoutSessionExercise getOrThrow(int sessionId, int workoutSessionExerciseId) {
        Optional<WorkoutSessionExercise> sessionExercise =
                workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(workoutSessionExerciseId, sessionId);
        return sessionExercise.orElseThrow(
                () -> new WorkoutSessionExerciseNotFoundException(workoutSessionExerciseId, sessionId));
    }

    private WorkoutSessionExerciseResponse toResponse(WorkoutSessionExercise sessionExercise) {
        List<SetLogResponse> setResponses = setLogRepository
                .findByWorkoutSessionExerciseIdOrderBySetIndexAsc(sessionExercise.getId())
                .stream()
                .map(setLog -> new SetLogResponse(
                        setLog.getId(), setLog.getSetIndex(), setLog.getReps(), setLog.getWeightKg()))
                .toList();

        Exercise exercise = sessionExercise.getExercise();
        return new WorkoutSessionExerciseResponse(
                sessionExercise.getId(),
                exercise.getId(),
                exercise.getName(),
                sessionExercise.getPosition(),
                setResponses);
    }
}

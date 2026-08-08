package com.test.fit_app.service;

import com.test.fit_app.dto.SetLogResponse;
import com.test.fit_app.dto.WorkoutSessionExerciseResponse;
import com.test.fit_app.dto.WorkoutSessionRequest;
import com.test.fit_app.dto.WorkoutSessionResponse;
import com.test.fit_app.exception.WorkoutSessionNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.model.SetLog;
import com.test.fit_app.model.WorkoutSession;
import com.test.fit_app.model.WorkoutSessionExercise;
import com.test.fit_app.model.enums.Musclegroups;
import com.test.fit_app.repository.SetLogRepository;
import com.test.fit_app.repository.WorkoutSessionExerciseRepository;
import com.test.fit_app.repository.WorkoutSessionRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutSessionExerciseRepository workoutSessionExerciseRepository;
    private final SetLogRepository setLogRepository;

    @Transactional
    public WorkoutSessionResponse create(WorkoutSessionRequest request) {
        WorkoutSession session = new WorkoutSession();
        session.setName(request.getName());
        session.setDate(request.getDate());
        return toResponse(workoutSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public WorkoutSessionResponse findById(int id) {
        return toResponse(getSessionOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<WorkoutSessionResponse> findAll() {
        return workoutSessionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WorkoutSessionResponse update(int id, WorkoutSessionRequest request) {
        WorkoutSession session = getSessionOrThrow(id);
        session.setName(request.getName());
        session.setDate(request.getDate());
        return toResponse(workoutSessionRepository.save(session));
    }

    @Transactional
    public void delete(int id) {
        WorkoutSession session = getSessionOrThrow(id);
        List<WorkoutSessionExercise> exercises =
                workoutSessionExerciseRepository.findByWorkoutSessionIdOrderByPositionAsc(id);
        for (WorkoutSessionExercise exercise : exercises) {
            setLogRepository.deleteAll(
                    setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(exercise.getId()));
        }
        workoutSessionExerciseRepository.deleteAll(exercises);
        workoutSessionRepository.delete(session);
    }

    private WorkoutSession getSessionOrThrow(int id) {
        Optional<WorkoutSession> session = workoutSessionRepository.findById(id);
        return session.orElseThrow(() -> new WorkoutSessionNotFoundException(id));
    }

    private WorkoutSessionResponse toResponse(WorkoutSession session) {
        List<WorkoutSessionExercise> sessionExercises =
                workoutSessionExerciseRepository.findByWorkoutSessionIdOrderByPositionAsc(session.getId());

        List<WorkoutSessionExerciseResponse> exerciseResponses =
                sessionExercises.stream().map(this::toExerciseResponse).toList();

        Set<Musclegroups> muscleGroupsHit = sessionExercises.stream()
                .map(sessionExercise -> sessionExercise.getExercise().getMuscleGroup())
                .collect(Collectors.toSet());

        return new WorkoutSessionResponse(
                session.getId(), session.getName(), session.getDate(), muscleGroupsHit, exerciseResponses);
    }

    private WorkoutSessionExerciseResponse toExerciseResponse(WorkoutSessionExercise sessionExercise) {
        List<SetLogResponse> setResponses = setLogRepository
                .findByWorkoutSessionExerciseIdOrderBySetIndexAsc(sessionExercise.getId())
                .stream()
                .map(this::toSetLogResponse)
                .toList();

        Exercise exercise = sessionExercise.getExercise();
        return new WorkoutSessionExerciseResponse(
                sessionExercise.getId(),
                exercise.getId(),
                exercise.getName(),
                sessionExercise.getPosition(),
                setResponses);
    }

    private SetLogResponse toSetLogResponse(SetLog setLog) {
        return new SetLogResponse(setLog.getId(), setLog.getSetIndex(), setLog.getReps(), setLog.getWeightKg());
    }
}

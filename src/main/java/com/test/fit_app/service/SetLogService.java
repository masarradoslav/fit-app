package com.test.fit_app.service;

import com.test.fit_app.dto.SetLogRequest;
import com.test.fit_app.dto.SetLogResponse;
import com.test.fit_app.exception.SetLogNotFoundException;
import com.test.fit_app.exception.WorkoutSessionExerciseNotFoundException;
import com.test.fit_app.model.SetLog;
import com.test.fit_app.model.WorkoutSessionExercise;
import com.test.fit_app.repository.SetLogRepository;
import com.test.fit_app.repository.WorkoutSessionExerciseRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetLogService {

    private final WorkoutSessionExerciseRepository workoutSessionExerciseRepository;
    private final SetLogRepository setLogRepository;

    @Transactional
    public SetLogResponse addSet(int sessionId, int workoutSessionExerciseId, SetLogRequest request) {
        WorkoutSessionExercise sessionExercise = getSessionExerciseOrThrow(sessionId, workoutSessionExerciseId);

        SetLog setLog = new SetLog();
        setLog.setWorkoutSessionExercise(sessionExercise);
        setLog.setSetIndex(setLogRepository.countByWorkoutSessionExerciseId(workoutSessionExerciseId) + 1);
        setLog.setReps(request.getReps());
        setLog.setWeightKg(request.getWeightKg());

        return toResponse(setLogRepository.save(setLog));
    }

    @Transactional
    public SetLogResponse updateSet(int sessionId, int workoutSessionExerciseId, int setLogId, SetLogRequest request) {
        getSessionExerciseOrThrow(sessionId, workoutSessionExerciseId);
        SetLog setLog = getSetLogOrThrow(workoutSessionExerciseId, setLogId);

        setLog.setReps(request.getReps());
        setLog.setWeightKg(request.getWeightKg());

        return toResponse(setLogRepository.save(setLog));
    }

    @Transactional
    public void deleteSet(int sessionId, int workoutSessionExerciseId, int setLogId) {
        getSessionExerciseOrThrow(sessionId, workoutSessionExerciseId);
        SetLog setLog = getSetLogOrThrow(workoutSessionExerciseId, setLogId);
        int deletedIndex = setLog.getSetIndex();
        setLogRepository.delete(setLog);

        for (SetLog laterSet :
                setLogRepository.findByWorkoutSessionExerciseIdOrderBySetIndexAsc(workoutSessionExerciseId)) {
            if (laterSet.getSetIndex() > deletedIndex) {
                laterSet.setSetIndex(laterSet.getSetIndex() - 1);
            }
        }
    }

    private WorkoutSessionExercise getSessionExerciseOrThrow(int sessionId, int workoutSessionExerciseId) {
        Optional<WorkoutSessionExercise> sessionExercise =
                workoutSessionExerciseRepository.findByIdAndWorkoutSessionId(workoutSessionExerciseId, sessionId);
        return sessionExercise.orElseThrow(
                () -> new WorkoutSessionExerciseNotFoundException(workoutSessionExerciseId, sessionId));
    }

    private SetLog getSetLogOrThrow(int workoutSessionExerciseId, int setLogId) {
        Optional<SetLog> setLog =
                setLogRepository.findByIdAndWorkoutSessionExerciseId(setLogId, workoutSessionExerciseId);
        return setLog.orElseThrow(() -> new SetLogNotFoundException(setLogId, workoutSessionExerciseId));
    }

    private SetLogResponse toResponse(SetLog setLog) {
        return new SetLogResponse(setLog.getId(), setLog.getSetIndex(), setLog.getReps(), setLog.getWeightKg());
    }
}

package com.test.fit_app.service;

import com.test.fit_app.dto.ExerciseRequest;
import com.test.fit_app.dto.ExerciseResponse;
import com.test.fit_app.exception.ExerciseNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.repository.ExerciseRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseResponse create(ExerciseRequest request) {
        Exercise exercise = new Exercise();
        exercise.setName(request.getName());
        exercise.setMuscleGroup(request.getMuscleGroup());
        exercise.setMuscle(request.getMuscle());
        return toResponse(exerciseRepository.save(exercise));
    }

    public ExerciseResponse findById(int id) {
        return toResponse(getExerciseOrThrow(id));
    }

    public List<ExerciseResponse> findAll() {
        return exerciseRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ExerciseResponse update(int id, ExerciseRequest request) {
        Exercise exercise = getExerciseOrThrow(id);
        exercise.setName(request.getName());
        exercise.setMuscleGroup(request.getMuscleGroup());
        exercise.setMuscle(request.getMuscle());
        return toResponse(exerciseRepository.save(exercise));
    }

    public void delete(int id) {
        Exercise exercise = getExerciseOrThrow(id);
        exerciseRepository.delete(exercise);
    }

    private Exercise getExerciseOrThrow(int id) {
        Optional<Exercise> exercise = exerciseRepository.findById(id);
        return exercise.orElseThrow(() -> new ExerciseNotFoundException(id));
    }

    private ExerciseResponse toResponse(Exercise exercise) {
        return new ExerciseResponse(
                exercise.getId(),
                exercise.getName(),
                exercise.getMuscleGroup(),
                exercise.getMuscle());
    }
}

package com.test.fit_app.repository;

import com.test.fit_app.model.WorkoutSessionExercise;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSessionExerciseRepository extends JpaRepository<WorkoutSessionExercise, Integer> {

    List<WorkoutSessionExercise> findByWorkoutSessionIdOrderByPositionAsc(int workoutSessionId);

    int countByWorkoutSessionId(int workoutSessionId);

    Optional<WorkoutSessionExercise> findByIdAndWorkoutSessionId(int id, int workoutSessionId);
}

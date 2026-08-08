package com.test.fit_app.repository;

import com.test.fit_app.model.SetLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetLogRepository extends JpaRepository<SetLog, Integer> {

    List<SetLog> findByWorkoutSessionExerciseIdOrderBySetIndexAsc(int workoutSessionExerciseId);

    int countByWorkoutSessionExerciseId(int workoutSessionExerciseId);

    Optional<SetLog> findByIdAndWorkoutSessionExerciseId(int id, int workoutSessionExerciseId);
}

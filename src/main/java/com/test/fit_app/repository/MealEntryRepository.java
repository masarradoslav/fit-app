package com.test.fit_app.repository;

import com.test.fit_app.model.MealEntry;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Integer> {

    List<MealEntry> findByLoggedAtGreaterThanEqualAndLoggedAtLessThanOrderByLoggedAtAsc(
            LocalDateTime start, LocalDateTime end);
}

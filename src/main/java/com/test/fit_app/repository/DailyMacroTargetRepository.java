package com.test.fit_app.repository;

import com.test.fit_app.model.DailyMacroTarget;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyMacroTargetRepository extends JpaRepository<DailyMacroTarget, Integer> {

    // Only one row ever exists.
    Optional<DailyMacroTarget> findFirstByOrderByIdAsc();
}

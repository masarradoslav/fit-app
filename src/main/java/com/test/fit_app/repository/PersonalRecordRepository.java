package com.test.fit_app.repository;

import com.test.fit_app.model.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Integer> {
}

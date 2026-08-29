package com.test.fit_app.service;

import com.test.fit_app.dto.PersonalRecordRequest;
import com.test.fit_app.dto.PersonalRecordResponse;
import com.test.fit_app.exception.ExerciseNotFoundException;
import com.test.fit_app.exception.PersonalRecordNotFoundException;
import com.test.fit_app.model.Exercise;
import com.test.fit_app.model.PersonalRecord;
import com.test.fit_app.repository.ExerciseRepository;
import com.test.fit_app.repository.PersonalRecordRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalRecordService {

    private final PersonalRecordRepository personalRecordRepository;
    private final ExerciseRepository exerciseRepository;

    public PersonalRecordResponse create(PersonalRecordRequest request) {
        Exercise exercise = getExerciseOrThrow(request.getExerciseId());

        PersonalRecord personalRecord = new PersonalRecord();
        personalRecord.setExercise(exercise);
        personalRecord.setUserId(request.getUserId());
        personalRecord.setDate(request.getDate());
        personalRecord.setWeightKg(request.getWeightKg());

        return toResponse(personalRecordRepository.save(personalRecord));
    }

    public PersonalRecordResponse findById(int id) {
        return toResponse(getPersonalRecordOrThrow(id));
    }

    public List<PersonalRecordResponse> findAll() {
        return personalRecordRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PersonalRecordResponse update(int id, PersonalRecordRequest request) {
        PersonalRecord personalRecord = getPersonalRecordOrThrow(id);
        Exercise exercise = getExerciseOrThrow(request.getExerciseId());

        personalRecord.setExercise(exercise);
        personalRecord.setUserId(request.getUserId());
        personalRecord.setDate(request.getDate());
        personalRecord.setWeightKg(request.getWeightKg());

        return toResponse(personalRecordRepository.save(personalRecord));
    }

    public void delete(int id) {
        PersonalRecord personalRecord = getPersonalRecordOrThrow(id);
        personalRecordRepository.delete(personalRecord);
    }

    private Exercise getExerciseOrThrow(int exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseNotFoundException(exerciseId));
    }

    private PersonalRecord getPersonalRecordOrThrow(int id) {
        Optional<PersonalRecord> personalRecord = personalRecordRepository.findById(id);
        return personalRecord.orElseThrow(() -> new PersonalRecordNotFoundException(id));
    }

    private PersonalRecordResponse toResponse(PersonalRecord personalRecord) {
        Exercise exercise = personalRecord.getExercise();
        return new PersonalRecordResponse(
                personalRecord.getId(),
                exercise.getId(),
                exercise.getName(),
                personalRecord.getUserId(),
                personalRecord.getDate(),
                personalRecord.getWeightKg());
    }
}

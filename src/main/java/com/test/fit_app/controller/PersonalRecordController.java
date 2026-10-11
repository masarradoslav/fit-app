package com.test.fit_app.controller;

import com.test.fit_app.dto.PersonalRecordRequest;
import com.test.fit_app.dto.PersonalRecordResponse;
import com.test.fit_app.service.PersonalRecordService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personal-records")
@RequiredArgsConstructor
public class PersonalRecordController {

    private final PersonalRecordService personalRecordService;

    @PostMapping
    public ResponseEntity<PersonalRecordResponse> create(@Valid @RequestBody PersonalRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personalRecordService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalRecordResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(personalRecordService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PersonalRecordResponse>> findAll() {
        return ResponseEntity.ok(personalRecordService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalRecordResponse> update(@PathVariable int id, @Valid @RequestBody PersonalRecordRequest request) {
        return ResponseEntity.ok(personalRecordService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        personalRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

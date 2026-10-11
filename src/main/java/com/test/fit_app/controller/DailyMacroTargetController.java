package com.test.fit_app.controller;

import com.test.fit_app.dto.DailyMacroTargetRequest;
import com.test.fit_app.dto.DailyMacroTargetResponse;
import com.test.fit_app.service.DailyMacroTargetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/daily-macro-target")
@RequiredArgsConstructor
public class DailyMacroTargetController {

    private final DailyMacroTargetService dailyMacroTargetService;

    @GetMapping
    public ResponseEntity<DailyMacroTargetResponse> getCurrent() {
        return ResponseEntity.ok(dailyMacroTargetService.getCurrent());
    }

    @PutMapping
    public ResponseEntity<DailyMacroTargetResponse> setTarget(@Valid @RequestBody DailyMacroTargetRequest request) {
        return ResponseEntity.ok(dailyMacroTargetService.setTarget(request));
    }
}

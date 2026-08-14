package com.test.fit_app.service;

import com.test.fit_app.dto.DailyMacroTargetRequest;
import com.test.fit_app.dto.DailyMacroTargetResponse;
import com.test.fit_app.exception.DailyMacroTargetNotFoundException;
import com.test.fit_app.model.DailyMacroTarget;
import com.test.fit_app.repository.DailyMacroTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyMacroTargetService {

    private final DailyMacroTargetRepository dailyMacroTargetRepository;

    public DailyMacroTargetResponse getCurrent() {
        return toResponse(dailyMacroTargetRepository
                .findFirstByOrderByIdAsc()
                .orElseThrow(DailyMacroTargetNotFoundException::new));
    }

    public DailyMacroTargetResponse setTarget(DailyMacroTargetRequest request) {
        DailyMacroTarget target = dailyMacroTargetRepository.findFirstByOrderByIdAsc().orElseGet(DailyMacroTarget::new);
        target.setTargetProtein(request.getTargetProtein());
        target.setTargetCarbs(request.getTargetCarbs());
        target.setTargetFat(request.getTargetFat());
        target.setTargetCalories(request.getTargetCalories());
        return toResponse(dailyMacroTargetRepository.save(target));
    }

    private DailyMacroTargetResponse toResponse(DailyMacroTarget target) {
        return new DailyMacroTargetResponse(
                target.getId(),
                target.getTargetProtein(),
                target.getTargetCarbs(),
                target.getTargetFat(),
                target.getTargetCalories());
    }
}

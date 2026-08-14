package com.test.fit_app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.fit_app.dto.DailyMacroTargetRequest;
import com.test.fit_app.dto.DailyMacroTargetResponse;
import com.test.fit_app.exception.DailyMacroTargetNotFoundException;
import com.test.fit_app.model.DailyMacroTarget;
import com.test.fit_app.repository.DailyMacroTargetRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailyMacroTargetServiceTest {

    @Mock
    private DailyMacroTargetRepository dailyMacroTargetRepository;

    @InjectMocks
    private DailyMacroTargetService dailyMacroTargetService;

    @Test
    void getCurrent_shouldReturnMappedResponse_whenTargetExists() {
        DailyMacroTarget target = new DailyMacroTarget(1, 150.0, 200.0, 60.0, 2000.0);
        when(dailyMacroTargetRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(target));

        DailyMacroTargetResponse response = dailyMacroTargetService.getCurrent();

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getTargetProtein()).isEqualTo(150.0);
        assertThat(response.getTargetCarbs()).isEqualTo(200.0);
        assertThat(response.getTargetFat()).isEqualTo(60.0);
        assertThat(response.getTargetCalories()).isEqualTo(2000.0);
    }

    @Test
    void getCurrent_shouldThrowDailyMacroTargetNotFoundException_whenNoneExists() {
        when(dailyMacroTargetRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dailyMacroTargetService.getCurrent())
                .isInstanceOf(DailyMacroTargetNotFoundException.class);
    }

    @Test
    void setTarget_shouldCreateNewTarget_whenNoneExists() {
        when(dailyMacroTargetRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        DailyMacroTargetRequest request = new DailyMacroTargetRequest(150.0, 200.0, 60.0, 2000.0);
        ArgumentCaptor<DailyMacroTarget> captor = ArgumentCaptor.forClass(DailyMacroTarget.class);
        when(dailyMacroTargetRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        DailyMacroTargetResponse response = dailyMacroTargetService.setTarget(request);

        assertThat(captor.getValue().getId()).isEqualTo(0);
        assertThat(response.getTargetProtein()).isEqualTo(150.0);
        assertThat(response.getTargetCarbs()).isEqualTo(200.0);
        assertThat(response.getTargetFat()).isEqualTo(60.0);
        assertThat(response.getTargetCalories()).isEqualTo(2000.0);
    }

    @Test
    void setTarget_shouldUpdateExistingTargetInPlace_whenOneExists() {
        DailyMacroTarget existing = new DailyMacroTarget(1, 100.0, 150.0, 50.0, 1800.0);
        DailyMacroTargetRequest request = new DailyMacroTargetRequest(150.0, 200.0, 60.0, 2000.0);
        when(dailyMacroTargetRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(existing));
        when(dailyMacroTargetRepository.save(any(DailyMacroTarget.class))).thenReturn(existing);

        DailyMacroTargetResponse response = dailyMacroTargetService.setTarget(request);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getTargetProtein()).isEqualTo(150.0);
        assertThat(existing.getTargetCarbs()).isEqualTo(200.0);
        verify(dailyMacroTargetRepository).save(existing);
    }
}

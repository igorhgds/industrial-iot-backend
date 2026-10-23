package henrique.igor.iiot.application.usecases.automation;

import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.domain.entities.enums.ActionType;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AutomationRuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleAutomationRuleUseCaseTest {

    @Mock
    private AutomationRuleRepository automationRuleRepository;

    @InjectMocks
    private ToggleAutomationRuleUseCase toggleAutomationRuleUseCase;

    @Test
    @DisplayName("Should toggle automation rule from inactive to active and vice versa")
    void shouldToggleAutomationRuleSuccessfully() {
        UUID automationId = UUID.randomUUID();
        AutomationRule rule = new AutomationRule(automationId, "Emergency Stop", null, null, BigDecimal.TEN, ActionType.TOGGLE_ACTUATOR, null, false);

        when(automationRuleRepository.findById(automationId)).thenReturn(Optional.of(rule));
        when(automationRuleRepository.save(any(AutomationRule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AutomationRule activated = toggleAutomationRuleUseCase.execute(automationId);
        assertTrue(activated.getActive());

        AutomationRule deactivated = toggleAutomationRuleUseCase.execute(automationId);
        assertFalse(deactivated.getActive());

        verify(automationRuleRepository, times(2)).save(rule);
    }

    @Test
    @DisplayName("Should throw exception when automationId is null")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(DomainValidationException.class, () -> toggleAutomationRuleUseCase.execute(null));
        verify(automationRuleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when automation rule is not found")
    void shouldThrowExceptionWhenRuleNotFound() {
        UUID automationId = UUID.randomUUID();
        when(automationRuleRepository.findById(automationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> toggleAutomationRuleUseCase.execute(automationId));
    }
}

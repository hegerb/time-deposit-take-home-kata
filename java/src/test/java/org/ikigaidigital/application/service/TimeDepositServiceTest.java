package org.ikigaidigital.application.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.PlanTypes;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.Withdrawal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeDepositServiceTest {

    @Mock
    private TimeDepositRepository repository;

    private TimeDepositService service;

    @BeforeEach
    void createService() {
        service = new TimeDepositService(repository, new TimeDepositCalculator());
    }

    @Test
    void should_returnRepositoryContentUnchanged_whenAskedForAllTimeDeposits() {
        List<TimeDepositWithWithdrawals> stored = List.of(basicDepositPastGracePeriod());
        when(repository.findAll()).thenReturn(stored);

        List<TimeDepositWithWithdrawals> all = service.getAllTimeDeposits();

        assertThat(all).isSameAs(stored);
    }

    @Test
    void should_creditOneMonthOfInterestAndSaveIt_whenUpdatingAllBalances() {
        TimeDepositWithWithdrawals stored = basicDepositPastGracePeriod();
        when(repository.findAll()).thenReturn(List.of(stored));

        List<TimeDepositWithWithdrawals> updated = service.updateAllBalances();

        assertThat(updated).containsExactly(stored);
        assertThat(stored.timeDeposit().getBalance()).isEqualTo(1000.83);
        verify(repository).saveBalances(List.of(stored.timeDeposit()));
    }

    @Test
    void should_saveEmptyListAndReturnEmpty_whenThereAreNoTimeDeposits() {
        when(repository.findAll()).thenReturn(List.of());

        List<TimeDepositWithWithdrawals> updated = service.updateAllBalances();

        assertThat(updated).isEmpty();
        verify(repository).saveBalances(List.of());
    }

    private static TimeDepositWithWithdrawals basicDepositPastGracePeriod() {
        TimeDeposit timeDeposit = new TimeDeposit(1, PlanTypes.BASIC, 1000.00, 31);
        Withdrawal withdrawal = new Withdrawal(7, new BigDecimal("50.00"), LocalDate.of(2024, 3, 1));
        return new TimeDepositWithWithdrawals(timeDeposit, List.of(withdrawal));
    }
}

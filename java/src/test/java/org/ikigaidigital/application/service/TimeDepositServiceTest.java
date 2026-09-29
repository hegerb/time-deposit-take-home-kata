package org.ikigaidigital.application.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.PlanTypes;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.Withdrawal;
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

    @Test
    void should_returnEverythingTheRepositoryHolds_whenAskedForAllTimeDeposits() {
        List<TimeDepositWithWithdrawals> stored = List.of(basicDeposit(1000.00, 31));
        when(repository.findAll()).thenReturn(stored);
        TimeDepositService service = new TimeDepositService(repository, new TimeDepositCalculator());

        List<TimeDepositWithWithdrawals> all = service.getAllTimeDeposits();

        assertThat(all).isEqualTo(stored);
    }

    @Test
    void should_creditOneMonthOfInterestAndSaveIt_whenUpdatingAllBalances() {
        TimeDepositWithWithdrawals stored = basicDeposit(1000.00, 31);
        when(repository.findAll()).thenReturn(List.of(stored));
        TimeDepositService service = new TimeDepositService(repository, new TimeDepositCalculator());

        List<TimeDepositWithWithdrawals> updated = service.updateAllBalances();

        assertThat(updated).containsExactly(stored);
        assertThat(stored.timeDeposit().getBalance()).isEqualTo(1000.83);
        verify(repository).saveBalances(List.of(stored.timeDeposit()));
    }

    @Test
    void should_keepWithdrawalsUntouched_whenUpdatingAllBalances() {
        TimeDepositWithWithdrawals stored = basicDeposit(1000.00, 31);
        when(repository.findAll()).thenReturn(List.of(stored));
        TimeDepositService service = new TimeDepositService(repository, new TimeDepositCalculator());

        List<TimeDepositWithWithdrawals> updated = service.updateAllBalances();

        assertThat(updated.get(0).withdrawals()).isEqualTo(stored.withdrawals());
    }

    @Test
    void should_saveNothingButSucceed_whenThereAreNoTimeDeposits() {
        when(repository.findAll()).thenReturn(List.of());
        TimeDepositService service = new TimeDepositService(repository, new TimeDepositCalculator());

        List<TimeDepositWithWithdrawals> updated = service.updateAllBalances();

        assertThat(updated).isEmpty();
        verify(repository).saveBalances(List.of());
    }

    private static TimeDepositWithWithdrawals basicDeposit(double balance, int days) {
        TimeDeposit timeDeposit = new TimeDeposit(1, PlanTypes.BASIC, balance, days);
        Withdrawal withdrawal = new Withdrawal(7, new BigDecimal("50.00"), LocalDate.of(2024, 3, 1));
        return new TimeDepositWithWithdrawals(timeDeposit, List.of(withdrawal));
    }
}

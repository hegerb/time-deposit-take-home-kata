package org.ikigaidigital.domain.model;

import org.ikigaidigital.TimeDeposit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimeDepositWithWithdrawalsTest {

    @Test
    void should_keepItsOwnWithdrawals_whenTheSourceListChangesAfterwards() {
        List<Withdrawal> source = new ArrayList<>();
        source.add(new Withdrawal(1, new BigDecimal("10.00"), LocalDate.of(2024, 1, 1)));
        TimeDepositWithWithdrawals timeDeposit =
                new TimeDepositWithWithdrawals(new TimeDeposit(1, PlanTypes.BASIC, 100.00, 31), source);

        source.clear();

        assertThat(timeDeposit.withdrawals()).hasSize(1);
    }
}

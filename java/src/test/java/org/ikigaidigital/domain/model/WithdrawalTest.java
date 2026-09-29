package org.ikigaidigital.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class WithdrawalTest {

    @Test
    void should_storeAmountWithTwoDecimals_whenGivenAnotherScale() {
        Withdrawal withdrawal = new Withdrawal(1, new BigDecimal("50"), LocalDate.of(2024, 1, 1));

        assertThat(withdrawal.amount()).isEqualTo(new BigDecimal("50.00"));
    }
}

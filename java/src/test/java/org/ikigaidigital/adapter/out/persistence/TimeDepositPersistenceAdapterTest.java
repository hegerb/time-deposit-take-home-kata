package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.TestcontainersConfiguration;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.PlanTypes;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.Withdrawal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, TimeDepositPersistenceAdapter.class})
class TimeDepositPersistenceAdapterTest {

    @Autowired
    private TimeDepositJpaRepository timeDeposits;

    @Autowired
    private WithdrawalJpaRepository withdrawals;

    @Autowired
    private TimeDepositPersistenceAdapter adapter;

    @BeforeEach
    void startFromAnEmptyDatabase() {
        withdrawals.deleteAll();
        timeDeposits.deleteAll();
    }

    @Test
    void should_returnDepositsInIdOrderWithTheirWithdrawals_whenReadingAll() {
        timeDeposits.save(new TimeDepositEntity(2, PlanTypes.PREMIUM, 50, new BigDecimal("200.00")));
        timeDeposits.save(new TimeDepositEntity(1, PlanTypes.BASIC, 31, new BigDecimal("100.00")));
        withdrawals.save(new WithdrawalEntity(10, 1, new BigDecimal("25.00"), LocalDate.of(2024, 5, 1)));
        withdrawals.save(new WithdrawalEntity(11, 1, new BigDecimal("5.50"), LocalDate.of(2024, 6, 1)));

        List<TimeDepositWithWithdrawals> all = adapter.findAll();

        assertThat(all).extracting(TimeDepositWithWithdrawals::timeDeposit)
                .extracting(TimeDeposit::getId, TimeDeposit::getPlanType, TimeDeposit::getBalance, TimeDeposit::getDays)
                .containsExactly(
                        tuple(1, PlanTypes.BASIC, 100.00, 31),
                        tuple(2, PlanTypes.PREMIUM, 200.00, 50));
        assertThat(all.get(0).withdrawals()).containsExactly(
                new Withdrawal(10, new BigDecimal("25.00"), LocalDate.of(2024, 5, 1)),
                new Withdrawal(11, new BigDecimal("5.50"), LocalDate.of(2024, 6, 1)));
        assertThat(all.get(1).withdrawals()).isEmpty();
    }

    @Test
    void should_persistNewBalancesWithTwoDecimals_whenSavingBalances() {
        timeDeposits.save(new TimeDepositEntity(1, PlanTypes.BASIC, 31, new BigDecimal("100.00")));
        timeDeposits.save(new TimeDepositEntity(2, PlanTypes.STUDENT, 31, new BigDecimal("300.00")));

        adapter.saveBalances(List.of(
                new TimeDeposit(1, PlanTypes.BASIC, 100.83, 31),
                new TimeDeposit(2, PlanTypes.STUDENT, 300.75, 31)));
        timeDeposits.flush();

        assertThat(timeDeposits.findById(1)).get().extracting(TimeDepositEntity::getBalance).isEqualTo(new BigDecimal("100.83"));
        assertThat(timeDeposits.findById(2)).get().extracting(TimeDepositEntity::getBalance).isEqualTo(new BigDecimal("300.75"));
    }

    @Test
    void should_returnEmptyList_whenThereAreNoDeposits() {
        assertThat(adapter.findAll()).isEmpty();
    }
}
